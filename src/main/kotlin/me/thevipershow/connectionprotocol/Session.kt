// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol

import me.thevipershow.connectionprotocol.event.session.SessionEvent
import me.thevipershow.connectionprotocol.event.session.SessionListener
import me.thevipershow.connectionprotocol.packet.Packet
import me.thevipershow.connectionprotocol.packet.PacketProtocol
import java.net.SocketAddress

interface Session {
    fun connect()

    fun connect(wait: Boolean)

    fun getHost(): String

    fun getPort(): Int

    fun getLocalAddress(): SocketAddress?

    fun getRemoteAddress(): SocketAddress?

    fun getPacketProtocol(): PacketProtocol

    fun getFlags(): Map<String, Any>

    fun hasFlag(key: String): Boolean

    fun <T> getFlag(key:String): T?

    fun <T> getFlag(key: String, def: T): T?

    fun setFlag(key: String, value: Any)

    fun getListeners(): List<SessionListener>

    fun addListener(listener: SessionListener)

    fun removeListener(listener: SessionListener)

    fun callEvent(event: SessionEvent)

    fun getCompressionThreshold(): Int

    fun setCompressionThreshold(threshold: Int)

    fun getConnectionTimeout(): Int

    fun setConnectionTimeout(timeout: Int)

    fun getReadTimeout(): Int

    fun setReadTimeout(timeout: Int)

    fun getWriteTimeout(): Int

    fun setWriteTimeout(timeout: Int)

    fun isConnected(): Boolean

    fun send(packet: Packet)

    fun disconnect(reason: String?)

    fun disconnect(reason: String?, cause: Throwable?)
}