/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.packets.tcp.io

import io.netty.buffer.ByteBuf
import me.thevipershow.connectionprotocol.packets.io.NetOut
import java.io.IOException
import java.util.UUID

class ByteBufNetOut(private val buf: ByteBuf) : NetOut {
    override fun writeBoolean(boolean: Boolean) {
        this.buf.writeBoolean(boolean)
    }

    override fun writeByte(byte: Int) {
        this.buf.writeByte(byte)
    }

    override fun writeShort(short: Int) {
        this.buf.writeShort(short)
    }

    override fun writeChar(char: Int) {
        this.buf.writeChar(char)
    }

    override fun writeInt(int: Int) {
        this.buf.writeInt(int)
    }

    override fun writeVarInt(int: Int) {
        var i = int;
        while ((i and 0x7F.inv()) != 0) {
            this.writeByte((i and 0x7F) or 0x80)
            i = i ushr 7
        }

        this.writeByte(i)
    }

    override fun writeLong(long: Long) {
        this.buf.writeLong(long)
    }

    override fun writeVarLong(varLong: Long) {
        var l = varLong
        while ((l and 0x7F.inv()) != 0L) {
            this.writeByte(((l and 0x7F) or 0x80).toInt())
            l = l ushr 7
        }
        this.writeByte(l.toInt())
    }

    override fun writeFloat(float: Float) {
        this.buf.writeFloat(float)
    }

    override fun writeDouble(double: Double) {
        this.buf.writeDouble(double)
    }

    override fun writeBytes(byteArray: ByteArray) {
        this.buf.writeBytes(byteArray)
    }

    override fun writeBytes(byteArray: ByteArray, length: Int) {
        this.buf.writeBytes(byteArray, 0, length)
    }

    override fun writeShorts(shortArray: ShortArray) {
        this.writeShorts(shortArray, shortArray.size)
    }

    override fun writeShorts(shortArray: ShortArray, length: Int) {
        for (i in 0 until length) {
            this.writeShort(shortArray[i].toInt())
        }
    }

    override fun writeInts(intArray: IntArray) {
        this.writeInts(intArray, intArray.size)
    }

    override fun writeInts(intArray: IntArray, length: Int) {
        for (i in 0 until length) {
            this.writeInt(intArray[i])
        }
    }

    override fun writeLongs(longArray: LongArray) {
        this.writeLongs(longArray, longArray.size)
    }

    override fun writeLongs(longArray: LongArray, length: Int) {
        for (i in 0 until length) {
            this.writeLong(longArray[i])
        }
    }

    override fun writeString(string: String) {
        val bytes = string.toByteArray(Charsets.UTF_8)
        if (bytes.size > Short.MAX_VALUE) {
            throw IOException("String too big (was ${string.length} bytes encoded, max ${Short.MAX_VALUE})")
        } else {
            this.writeVarInt(bytes.size)
            this.writeBytes(bytes)
        }
    }

    override fun writeUUID(uuid: UUID) {
        this.writeLong(uuid.mostSignificantBits)
        this.writeLong(uuid.leastSignificantBits)
    }

    override fun flush() {
        //Empty method
    }
}