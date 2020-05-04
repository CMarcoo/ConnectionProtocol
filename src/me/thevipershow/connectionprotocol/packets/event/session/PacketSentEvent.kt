package me.thevipershow.connectionprotocol.packets.event.session

import me.thevipershow.connectionprotocol.packets.Session
import me.thevipershow.connectionprotocol.packets.packet.Packet
import kotlin.ClassCastException

class PacketSentEvent(val session: Session,private val packet: Packet) : SessionEvent {
    fun <T : Packet> getPacket(): T {
        try {
            return (this.packet as T)
        } catch (exc : ClassCastException) {
            throw IllegalStateException("Tried to get packet as the wrong type. Actual type: ${this.packet::class.qualifiedName}")
        }
    }

    override fun call(listener: SessionListener) {
        listener.packetSent(this)
    }
}