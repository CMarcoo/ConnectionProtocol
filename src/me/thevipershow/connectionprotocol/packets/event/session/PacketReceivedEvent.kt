package me.thevipershow.connectionprotocol.packets.event.session

import me.thevipershow.connectionprotocol.packets.Session
import me.thevipershow.connectionprotocol.packets.packet.Packet

class PacketReceivedEvent(val session: Session, private var packet: Packet) : SessionEvent {
    fun <T : Packet> getPacket(): T {
        try {
            return (packet as T)
        } catch (exc: ClassCastException) {
            throw IllegalStateException("Tried to get packet as the wrong type. Actual type: ${this.packet::class.qualifiedName}")
        }
    }

    fun setPacket(packet: Packet) {
        this.packet = packet
    }

    override fun call(listener: SessionListener) {
        listener.packetReceived(this)
    }
}