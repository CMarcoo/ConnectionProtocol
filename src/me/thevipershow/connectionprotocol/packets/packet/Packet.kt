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
import java.io.IOException

interface Packet {
    @Throws(IOException::class)
    fun read(netIn: NetIn)

    @Throws(IOException::class)
    fun write(netOut: NetOut)

    fun isPriority(): Boolean
}