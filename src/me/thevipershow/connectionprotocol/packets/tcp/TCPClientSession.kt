/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.packets.tcp

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.proxy.HttpProxyHandler
import io.netty.handler.proxy.Socks4ProxyHandler
import io.netty.handler.proxy.Socks5ProxyHandler
import me.thevipershow.connectionprotocol.packets.Client
import me.thevipershow.connectionprotocol.packets.ProxyInfo
import me.thevipershow.connectionprotocol.packets.packet.PacketProtocol
import org.xbill.DNS.Lookup
import org.xbill.DNS.SRVRecord
import org.xbill.DNS.TextParseException
import org.xbill.DNS.Type
import kotlin.IllegalStateException

class TCPClientSession(
    host: String,
    port: Int,
    protocol: PacketProtocol,
    private val client: Client,
    private val proxy: ProxyInfo?
) : TCPSession(host, port, protocol) {

    private var group: EventLoopGroup? = null

    override fun connect(wait: Boolean) {
        if (this.disconnected) {
            throw IllegalStateException("Session has already been disconnected")
        } else if (this.group == null) {
            try {
                this.group = NioEventLoopGroup()

                val bootstrap = Bootstrap()
                bootstrap.channel(NioSocketChannel::class.java)

                val channelInitializer = object : ChannelInitializer<Channel>() {
                    @Throws(Exception::class)
                    override fun initChannel(channel: Channel) {
                        getPacketProtocol().newClientSession(client, this@TCPClientSession)
                        channel.config().setOption(ChannelOption.IP_TOS, 0x18)
                        channel.config().setOption(ChannelOption.TCP_NODELAY, false)
                        val pipeline = channel.pipeline()
                        refreshReadTimeoutHandler(channel)
                        refreshWriteTimeoutHandler(channel)

                        if (proxy != null) {
                            when (proxy.getType()) {
                                ProxyInfo.Type.HTTP -> {
                                    if (proxy.isAuthenticated()) {
                                        pipeline.addFirst(
                                            "proxy",
                                            HttpProxyHandler(
                                                proxy.getAddress(),
                                                proxy.getUsername(),
                                                proxy.getPassword()
                                            )
                                        )
                                    } else {
                                        pipeline.addFirst("proxy", HttpProxyHandler(proxy.getAddress()))
                                    }
                                }
                                ProxyInfo.Type.SOCKS4 -> {
                                    if (proxy.isAuthenticated()) {
                                        pipeline.addFirst(
                                            "proxy",
                                            Socks4ProxyHandler(proxy.getAddress(), proxy.getUsername())
                                        )
                                    } else {
                                        pipeline.addFirst("proxy", Socks4ProxyHandler(proxy.getAddress()))
                                    }
                                }
                                ProxyInfo.Type.SOCKS5 -> {
                                    if (proxy.isAuthenticated()) {
                                        pipeline.addFirst(
                                            "proxy",
                                            Socks5ProxyHandler(
                                                proxy.getAddress(),
                                                proxy.getUsername(),
                                                proxy.getPassword()
                                            )
                                        )
                                    } else {
                                        pipeline.addFirst("proxy", Socks5ProxyHandler(proxy.getAddress()))
                                    }
                                }
                                else -> throw UnsupportedOperationException("Unsupported proxy type: ${proxy.getType()}")
                            }
                        }

                        pipeline.addLast("encryption", TCPPacketEncryptor(this@TCPClientSession))
                        pipeline.addLast("sizer", TCPPacketSizer(this@TCPClientSession))
                        pipeline.addLast("codec", TCPPacketCodec(this@TCPClientSession))
                        pipeline.addLast("manager", this@TCPClientSession)
                    }
                }
                bootstrap.handler(channelInitializer).group(this.group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectionTimeout() * 1000)

                val connectTask = Runnable {
                    run {
                        try {
                            var host = getHost()
                            var port = getPort()
                            try {
                                val records =
                                    Lookup("${getPacketProtocol().getSRVRecordPrefix()}._tcp.$host", Type.SRV).run()
                                if (records != null && records.isNotEmpty()) {
                                    val srv = records[0] as SRVRecord
                                    host = srv.target.toString().replaceFirst("\\.$", "")
                                    port = srv.port
                                }
                            } catch (exc: TextParseException) {
                            }

                            bootstrap.remoteAddress(host, port)
                            val future = bootstrap.connect().sync()
                            if (future.isSuccess) {
                                while (!isConnected() && !disconnected) {
                                    try {
                                        Thread.sleep(5)
                                    } catch (exc: InterruptedException) {
                                    }
                                }
                            }
                        } catch (t: Throwable) {
                            exceptionCaught(null, t)
                        }
                    }
                }
                if (wait) {
                    connectTask.run()
                } else {
                    Thread(connectTask).start()
                }
            } catch (t: Throwable) {
                exceptionCaught(null, t)
            }
        }

    }

    override fun disconnect(reason: String?, cause: Throwable?) {
        super.disconnect(reason, cause)
        if (this.group != null) {
            this.group!!.shutdownGracefully()
            this.group = null
        }
    }
}