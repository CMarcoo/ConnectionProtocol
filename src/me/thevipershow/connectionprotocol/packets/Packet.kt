package me.thevipershow.connectionprotocol.packets

import me.thevipershow.connectionprotocol.packets.io.NetIn
import me.thevipershow.connectionprotocol.packets.io.NetOut
import java.io.IOException

interface Packet {
    @Throws(IOException::class)
    fun read(netIn: NetIn)

    @Throws(IOException::class)
    fun write(netOut: NetOut)

    fun isPriority(): Boolean
}