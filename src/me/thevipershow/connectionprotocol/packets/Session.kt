package me.thevipershow.connectionprotocol.packets

import me.thevipershow.connectionprotocol.packets.event.session.SessionEvent
import me.thevipershow.connectionprotocol.packets.event.session.SessionListener
import me.thevipershow.connectionprotocol.packets.packet.Packet
import me.thevipershow.connectionprotocol.packets.packet.PacketProtocol
import java.net.SocketAddress

interface Session {
    fun connect()

    fun connect(wait:Boolean)

    fun getHost():String

    fun getPort():Int

    fun getLocalAddress():SocketAddress

    fun getRemoveAddress():SocketAddress

    fun getPacketProtocol():PacketProtocol

    fun getFlags(): Map<String, Any>

    fun hasFlag(key:String): Boolean

    fun <T> getFlag(key: String, def: T): T

    fun setFlag(key: String, value: Any)

    fun getListeners(): List<SessionListener>

    fun addListener(listener: SessionListener)

    fun removeListener(listener: SessionListener)

    fun callEvent(event: SessionEvent)

    fun getCompressionThreshold(): Int

    fun getConnectionTimeout(): Int

    fun setConnectionTimeout(timeout:Int)

    fun getReadTimeout(): Int

    fun setReadTimeout(timeout: Int)

    fun getWriteTimeout(): Int

    fun setWriteTimeout(timeout: Int)

    fun isConnected(): Boolean

    fun send(packet: Packet)

    fun disconnect(reason:String)

    fun disconnect(reason:String, cause: Throwable)
}