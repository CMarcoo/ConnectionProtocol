// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.packets.packet

import me.thevipershow.connectionprotocol.packets.io.NetIn
import me.thevipershow.connectionprotocol.packets.io.NetOut

class DefaultPacketHeader : PacketHeader {
    override fun isLengthVariable(): Boolean {
        return true
    }

    override fun getLengthSize(): Int {
        return 5
    }

    override fun getLengthSize(length: Int): Int {
        return when {
            (length and -128) == 0 -> 1
            (length and -16384) == 0 -> 2
            (length and -2097152) == 0 -> 3
            (length and -268435456) == 0 -> 4
            else -> 5
        }
    }

    override fun readLength(netIn: NetIn, available: Int): Int {
        return netIn.readVarInt()
    }

    override fun writeLength(netOut: NetOut, length: Int) {
        netOut.writeVarInt(length)
    }

    override fun readPacketID(netIn: NetIn): Int {
        return netIn.readVarInt()
    }

    override fun writePacketID(netOut: NetOut, ID: Int) {
        netOut.writeVarInt(ID)
    }
}