package me.thevipershow.connectionprotocol.packets

import me.thevipershow.connectionprotocol.packets.packet.PacketProtocol

class Client(val host: String, val port: Int, val protocol: PacketProtocol, factory: SessionFactory) {

}