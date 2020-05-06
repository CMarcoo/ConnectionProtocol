/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.packets.tcp

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ConnectTimeoutException
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutException
import io.netty.handler.timeout.WriteTimeoutHandler
import me.thevipershow.connectionprotocol.packets.Session
import me.thevipershow.connectionprotocol.packets.event.session.SessionListener
import me.thevipershow.connectionprotocol.packets.event.session.SessionEvent
import me.thevipershow.connectionprotocol.packets.event.session.PacketSendingEvent
import me.thevipershow.connectionprotocol.packets.event.session.PacketSentEvent
import me.thevipershow.connectionprotocol.packets.event.session.DisconnectedEvent
import me.thevipershow.connectionprotocol.packets.event.session.DisconnectingEvent
import me.thevipershow.connectionprotocol.packets.event.session.ConnectedEvent
import me.thevipershow.connectionprotocol.packets.event.session.PacketReceivedEvent
import me.thevipershow.connectionprotocol.packets.packet.Packet
import me.thevipershow.connectionprotocol.packets.packet.PacketProtocol
import java.net.ConnectException
import java.net.SocketAddress
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue


abstract class TCPSession(private var host: String, private var port: Int, val protocol: PacketProtocol) :
    SimpleChannelInboundHandler<Packet>(), Session {
    private var compressionThreshold = -1
    private var connectionTimeout = 30
    private var readTimeout = 30
    private var writeTimeout = 0
    private var channel: Channel? = null
    private var packetHandleThread: Thread? = null
    protected var disconnected = false

    private var flags = HashMap<String, Any>()
    private var listeners: MutableList<SessionListener> = CopyOnWriteArrayList()
    private var packets = LinkedBlockingQueue<Packet>()

    override fun connect() {
        this.connect(true)
    }

    override fun connect(wait: Boolean) {}

    override fun getHost(): String {
        return this.host
    }

    override fun getPort(): Int {
        return this.port
    }

    override fun getLocalAddress(): SocketAddress? {
        return if (this.channel != null) this.channel!!.localAddress() else null
    }

    override fun getRemoteAddress(): SocketAddress? {
        return if (this.channel != null) this.channel!!.remoteAddress() else null
    }

    override fun getPacketProtocol(): PacketProtocol {
        return this.protocol
    }

    override fun hasFlag(key: String): Boolean {
        return this.flags.containsKey(key)
    }

    override fun <T> getFlag(key: String): T? {
        return this.getFlag(key, null)
    }

    override fun getFlags(): Map<String, Any> {
        return HashMap<String,Any>(this.flags)
    }

    override fun isConnected(): Boolean {
        return this.channel != null && this.channel!!.isOpen && !this.disconnected
    }

    override fun <T> getFlag(key: String, def: T): T? {
        val value = this.getFlags()[key] ?: return def
        try {
            return value as T
        } catch (exc: ClassCastException) {
            throw IllegalStateException("Tried to get flag [$key] as the wrong type. Actual type ${value::class.qualifiedName}")
        }
    }

    override fun setFlag(key: String, value: Any) {
        this.flags[key] = value
    }

    override fun getListeners(): List<SessionListener> {
        return ArrayList<SessionListener>(this.listeners)
    }

    override fun addListener(listener: SessionListener) {
        this.listeners.add(listener)
    }

    override fun removeListener(listener: SessionListener) {
        this.listeners.remove(listener)
    }

    override fun callEvent(event: SessionEvent) {
        try {
            for (listener in this.listeners) {
                event.call(listener)
            }
        } catch (t: Throwable) {
            exceptionCaught(null, t)
        }
    }


    override fun getCompressionThreshold(): Int {
        return this.compressionThreshold
    }

    override fun setCompressionThreshold(threshold: Int) {
        this.compressionThreshold = threshold
        if (this.channel != null) {
            if (this.compressionThreshold >= 0) {
                if (this.channel!!.pipeline().get("compression") == null) {
                    this.channel!!.pipeline().addBefore("codec", "compression", TCPPacketCompression(this))
                }
            } else if (this.channel!!.pipeline()["compression"] != null) {
                this.channel!!.pipeline().remove("compression")
            }
        }
    }

    override fun setConnectionTimeout(timeout: Int) {
        this.connectionTimeout = timeout
    }

    override fun getConnectionTimeout(): Int {
        return this.connectionTimeout
    }

    override fun setReadTimeout(timeout: Int) {
        this.readTimeout = timeout
        this.refreshReadTimeoutHandler()
    }

    override fun getReadTimeout(): Int {
        return this.readTimeout
    }

    override fun setWriteTimeout(timeout: Int) {
        this.writeTimeout = timeout
    }

    override fun getWriteTimeout(): Int {
        return this.writeTimeout
    }

    override fun send(packet: Packet) {
        if (this.channel != null) {
            val sendingEvent = PacketSendingEvent(this, packet)
            this.callEvent(sendingEvent)
            if (!sendingEvent.cancelled) {
                val toSend: Packet = sendingEvent.getPacket()
                this.channel!!.writeAndFlush(toSend)
                    .addListener(ChannelFutureListener { future: ChannelFuture ->
                        run {
                            if (future.isSuccess) {
                                callEvent(PacketSentEvent(this@TCPSession, toSend))
                            } else {
                                exceptionCaught(null, future.cause())
                            }
                        }
                    })
            }
        }
    }

    override fun disconnect(reason: String?) {
        this.disconnect(reason, null)
    }

    override fun disconnect(reason: String?, cause: Throwable?) {
        if (!this.disconnected) {
            this.disconnected = true
            if (this.packetHandleThread != null) {
                this.packetHandleThread!!.interrupt()
                this.packetHandleThread = null
            }

            if (this.channel != null && this.channel!!.isOpen) {
                this.callEvent(DisconnectingEvent(reason, this, cause))
                this.channel!!.flush().close().addListener(ChannelFutureListener {
                    run {
                        callEvent(
                            DisconnectedEvent(reason ?: "Connection closed.", this@TCPSession, cause)
                        )
                    }
                })
            } else {
                this.callEvent(DisconnectedEvent(reason ?: "Connection closed", this@TCPSession, cause))
            }

            this.channel = null
        }
    }

    protected fun refreshReadTimeoutHandler() {
        this.refreshReadTimeoutHandler(this.channel)
    }

    protected fun refreshReadTimeoutHandler(channel: Channel?) {
        if (channel != null) {
            if (this.readTimeout <= 0) {
                if (channel.pipeline().get("readTimeout") != null) {
                    channel.pipeline().remove("readTimeout")
                }
            } else {
                if (channel.pipeline().get("readTimeout") == null) {
                    channel.pipeline().addFirst("readTimeout", ReadTimeoutHandler(this.readTimeout))
                } else {
                    channel.pipeline().replace("readTimeout", "readTimeout", ReadTimeoutHandler(this.readTimeout))
                }
            }
        }
    }

    protected fun refreshWriteTimeoutHandler() {
        this.refreshWriteTimeoutHandler(this.channel)
    }

    protected fun refreshWriteTimeoutHandler(channel: Channel?) {
        if (channel != null) {
            if (this.writeTimeout <= 0) {
                if (channel.pipeline().get("writeTimeout") != null) {
                    channel.pipeline().remove("writeTimeout")
                }
            } else {
                if (channel.pipeline().get("writeTimeout") == null) {
                    channel.pipeline().addFirst("writeTimeout", WriteTimeoutHandler(this.writeTimeout))
                } else {
                    channel.pipeline().replace("writeTimeout", "writeTimeout", WriteTimeoutHandler(this.writeTimeout))
                }
            }
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        if (this.disconnected || this.channel != null) {
            ctx.channel().close()
            return
        }

        this.channel = ctx.channel()
        this.packetHandleThread = Thread(Runnable {
            run {
                try {
                    var packet: Packet? = packets.take()
                    while (packet != null) {
                        callEvent(PacketReceivedEvent(this@TCPSession, packet))
                    }
                } catch (interrupted: InterruptedException) {
                } catch (throwable: Throwable) {
                    exceptionCaught(null, throwable)
                }
            }
        })

        this.packetHandleThread!!.start()
        this.callEvent(ConnectedEvent(this@TCPSession))
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        if (ctx.channel() == this.channel) {
            this.disconnect("Connection closed")
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable) {
        var message: String? = null
        message =
            if (cause is ConnectTimeoutException || (cause is ConnectException && cause.message!!.contains("connection timed out"))) {
                "Connection timed out."
            } else if (cause is ReadTimeoutException) {
                "Read timed out."
            } else if (cause is WriteTimeoutException) {
                "Write timed out."
            } else {
                cause.toString()
            }

        this.disconnect(message, cause)
    }

    override fun channelRead0(p0: ChannelHandlerContext?, p1: Packet?) {
        if (p1 != null && !p1.isPriority()) {
            this.packets.add(p1)
        }
    }
}