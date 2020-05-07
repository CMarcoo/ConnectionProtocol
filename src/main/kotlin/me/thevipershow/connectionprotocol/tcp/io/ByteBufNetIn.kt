/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.tcp.io

import io.netty.buffer.ByteBuf
import me.thevipershow.connectionprotocol.io.NetIn
import java.io.IOException
import java.util.*
import kotlin.experimental.and

class ByteBufNetIn(private val buf: ByteBuf) : NetIn {
    override fun readBoolean(): Boolean {
        return this.buf.readBoolean()
    }

    override fun readByte(): Byte {
        return this.buf.readByte()
    }

    override fun readUnsignedByte(): Int {
        return this.buf.readUnsignedByte().toInt()
    }

    override fun readShort(): Short {
        return this.buf.readShort()
    }

    override fun readUnsignedShort(): Int {
        return this.buf.readUnsignedShort()
    }

    override fun readChar(): Char {
        return this.buf.readChar()
    }

    override fun readInt(): Int {
        return this.buf.readInt()
    }

    override fun readVarInt(): Int {
        var value = 0
        var size = 0
        var byte: Byte = this.readByte()

        while ((byte and 0x80.toByte()) == 0x80.toByte()) {
            value = value or ((byte and 0x7F.toByte()).toInt() shl (size++ * 7))
            if (size > 5) {
                throw IOException("VarInt length is greater than 5")
            } else {
                byte = this.readByte()
            }
        }
        return value or ((byte and 0x7F).toInt() shl (size * 7))
    }

    override fun readLong(): Long {
        return this.buf.readLong()
    }

    override fun readVarLong(): Long {
        var value = 0L
        var size = 0
        var byte: Byte = this.readByte()

        while ((byte and 0x80.toByte()) == 0x80.toByte()) {
            value = value or ((byte and 0x7F.toByte()).toLong() shl (size++ * 7))
            if (size > 10) {
                throw IOException("VarInt length is greater than 10")
            } else {
                byte = this.readByte()
            }
        }
        return (value or ((byte and 0x7F).toLong() shl (size * 7)))
    }

    override fun readFloat(): Float {
        return this.buf.readFloat()
    }

    override fun readDouble(): Double {
        return this.buf.readDouble()
    }

    override fun readBytes(length: Int): ByteArray {
        if (length < 0) {
            throw IllegalArgumentException("Array size can't be < 0")
        }
        val bytes = ByteArray(length)
        this.buf.readBytes(bytes)
        return bytes
    }

    override fun readBytes(bytes: ByteArray): Int {
        return this.readBytes(bytes, 0, bytes.size)
    }

    override fun readBytes(bytes: ByteArray, offset: Int, length: Int): Int {
        val readable = this.buf.readableBytes()
        var l = length
        if (readable <= 0) {
            return -1
        } else if (readable < l) {
            l = readable
        }
        this.buf.readBytes(bytes, offset, l)
        return l
    }

    override fun readShorts(length: Int): ShortArray {
        if (length < 0) {
            throw IllegalArgumentException("Array size can't be < 0")
        }
        val shorts = ShortArray(length)
        for (i in 0 until length) {
            shorts[i] = this.readShort()
        }
        return shorts
    }

    override fun readShorts(shorts: ShortArray): Int {
        return this.readShorts(shorts, 0, shorts.size)
    }

    override fun readShorts(shorts: ShortArray, offset: Int, length: Int): Int {
        val readable = this.buf.readableBytes()
        var l = length
        if (readable <= 0) {
            return -1
        } else if (readable < l * 2) {
            l = readable / 2
        }
        for (i in offset until (offset + l)) {
            shorts[i] = this.readShort()
        }
        return l
    }

    override fun readInts(length: Int): IntArray {
        if (length < 0) {
            throw IllegalArgumentException("Array size can't be < 0")
        }
        val ints = IntArray(length)
        for (i in 0 until length) {
            ints[i] = this.readInt()
        }
        return ints
    }

    override fun readInts(ints: IntArray): Int {
        return this.readInts(ints, 0, ints.size)
    }

    override fun readInts(ints: IntArray, offset: Int, length: Int): Int {
        val readable = this.buf.readableBytes()
        var l = length
        if (readable <= 0) {
            return -1
        } else if (readable < l * 4) {
            l = readable / 4
        }
        for (i in offset until (offset + l)) {
            ints[i] = this.readInt()
        }
        return l
    }

    override fun readLongs(length: Int): LongArray {
        if (length < 0) {
            throw IllegalArgumentException("Array size can't be < 0")
        }
        val longs = LongArray(length)
        for (i in 0 until length) {
            longs[i] = this.readLong()
        }
        return longs
    }

    override fun readLongs(longs: LongArray): Int {
        return this.readLongs(longs, 0, longs.size)
    }

    override fun readLongs(longs: LongArray, offset: Int, length: Int): Int {
        val readable = this.buf.readableBytes()
        var l = length
        if (readable <= 0) {
            return -1
        } else if (readable < l * 2) {
            l = readable / 2
        }
        for (i in offset until (offset + l)) {
            longs[i] = this.readLong()
        }
        return l
    }

    override fun readString(): String {
        val length = this.readVarInt()
        val bytes = this.readBytes(length)
        return String(bytes, Charsets.UTF_8)
    }

    override fun readUUID(): UUID {
        return UUID(this.readLong(), this.readLong())
    }

    override fun available(): Int {
        return this.buf.readableBytes()
    }
}