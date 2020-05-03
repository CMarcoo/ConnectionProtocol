package packets

import packets.io.NetIn
import packets.io.NetOut
import java.io.IOException

interface Packet {
    @Throws(IOException::class)
    fun read(netIn: NetIn)

    @Throws(IOException::class)
    fun write(netOut: NetOut)

    fun isPriority(): Boolean
}