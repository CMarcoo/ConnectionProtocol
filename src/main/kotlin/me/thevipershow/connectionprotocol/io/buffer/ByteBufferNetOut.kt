// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.io.buffer

import me.thevipershow.connectionprotocol.io.NetOut
import java.io.IOException
import java.nio.ByteBuffer
import java.util.UUID

class ByteBufferNetOut(val byteBuffer: ByteBuffer) : NetOut {
    override fun writeBoolean(boolean: Boolean) {
        this.byteBuffer.put(if (boolean) 0x1.toByte() else 0x0.toByte())
    }

    override fun writeByte(byte: Int) {
        this.byteBuffer.put(byte.toByte())
    }

    override fun writeShort(short: Int) {
        this.byteBuffer.putShort(short.toShort())
    }

    override fun writeChar(char: Int) {
        this.byteBuffer.putChar(char.toChar())
    }

    override fun writeInt(int: Int) {
        this.byteBuffer.putInt(int)
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
        this.byteBuffer.putLong(long)
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
        this.byteBuffer.putFloat(float)
    }

    override fun writeDouble(double: Double) {
        this.byteBuffer.putDouble(double)
    }

    override fun writeBytes(byteArray: ByteArray) {
        this.byteBuffer.put(byteArray)
    }

    override fun writeBytes(byteArray: ByteArray, length: Int) {
        this.byteBuffer.put(byteArray, 0, length)
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
            throw IOException("String is too big ( ${bytes.size} bytes, max ${Short.MAX_VALUE} )")
        } else {
            this.writeVarInt(bytes.size)
            this.writeBytes(bytes)
        }
    }

    override fun writeUUID(uuid: UUID) {
        this.writeLong(uuid.mostSignificantBits)
        this.writeLong(uuid.leastSignificantBits)
    }

    override fun flush() {}
}