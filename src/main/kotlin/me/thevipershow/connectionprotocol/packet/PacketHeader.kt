// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.packet

import me.thevipershow.connectionprotocol.io.NetIn
import me.thevipershow.connectionprotocol.io.NetOut
import java.io.IOException

interface PacketHeader {
    /**
     * Gets whether the header's length value can vary in size
     *
     * @return Whether the header's length value can vary in size
     */
    fun isLengthVariable(): Boolean

    /**
     * Gets the size of the header's length value
     *
     * @return The length size value
     */
    fun getLengthSize(): Int

    /**
     * Gets the size of the header's length value
     *
     * @param length Length value to get the size of
     * @return The length value's size
     */
    fun getLengthSize(length: Int): Int

    /**
     * Reads the length of a packet from the given input.
     *
     * @param netIn        Input to read from.
     * @param available Number of packet bytes available after the length.
     * @return The resulting packet length.
     * @throws java.io.IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    fun readLength(netIn: NetIn, available: Int): Int

    /**
     * Writes the length of a packet to the given output.
     *
     * @param netOut    Output to write to.
     * @param length Length to write.
     * @throws java.io.IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    fun writeLength(netOut: NetOut, length: Int)

    /**
     * Reads the ID of a packet from the given input.
     *
     * @param netIn Input to read from.
     * @return The resulting packet ID, or -1 if the packet should not be read yet.
     * @throws java.io.IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    fun readPacketID(netIn: NetIn): Int

    /**
     * Writes the ID of a packet to the given output.
     *
     * @param netOut     Output to write to.
     * @param ID Packet ID to write.
     * @throws java.io.IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    fun writePacketID(netOut: NetOut, ID: Int)
}