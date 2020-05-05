/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.packets.tcp

import io.netty.channel.Channel
import io.netty.channel.SimpleChannelInboundHandler
import me.thevipershow.connectionprotocol.packets.Session
import me.thevipershow.connectionprotocol.packets.event.session.SessionEvent
import me.thevipershow.connectionprotocol.packets.event.session.SessionListener
import me.thevipershow.connectionprotocol.packets.packet.Packet
import me.thevipershow.connectionprotocol.packets.packet.PacketProtocol
import java.net.SocketAddress
import java.util.concurrent.CopyOnWriteArrayList


abstract class TCPSession(private var host: String, private var port: Int, val protocol: PacketProtocol) : SimpleChannelInboundHandler<Packet>(), Session {
    private var compressionThreshold = -1
    private var connectionTimeout = 30
    private var readTimeout = 30
    private var writeTimeout = 0

    private var flags = HashMap<String, Any>()
    private var listeners: MutableList<SessionListener> = CopyOnWriteArrayList()

    private lateinit var channel: Channel
    protected var disconnected = false

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
        return if (this.channel != null) this.channel.localAddress() else null
    }

    override fun getRemoteAddress(): SocketAddress? {
        return if (this.channel != null) this.channel.remoteAddress() else null
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
                if (this.channel.pipeline().get("compression") == null) {
                    this.channel.pipeline().addBefore("codec", "compression", )
                }
            }
        }
    }

    override fun setConnectionTimeout(timeout: Int) {
        TODO("Not yet implemented")
    }

    override fun getConnectionTimeout(): Int {
        TODO("Not yet implemented")
    }

    override fun setReadTimeout(timeout: Int) {
        TODO("Not yet implemented")
    }

    override fun getReadTimeout(): Int {
        TODO("Not yet implemented")
    }

    override fun setWriteTimeout(timeout: Int) {
        TODO("Not yet implemented")
    }

    override fun getWriteTimeout(): Int {
        TODO("Not yet implemented")
    }


}