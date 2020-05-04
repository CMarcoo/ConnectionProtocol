package me.thevipershow.connectionprotocol.packets.io.buffer

import me.thevipershow.connectionprotocol.packets.io.NetIn
import java.io.IOException
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.experimental.and

class ByteBufferNetIn(var buffer: ByteBuffer) : NetIn {
    override fun readBoolean(): Boolean {
        return this.buffer.get() == 1.toByte()
    }

    override fun readByte(): Byte {
        return this.buffer.get()
    }

    override fun readUnsignedByte(): Int {
        return (this.buffer.get() and 0xFF.toByte()).toInt()
    }

    override fun readShort(): Short {
        return this.buffer.short
    }

    override fun readUnsignedShort(): Int {
        return (this.buffer.short and 0xFFFF.toShort()).toInt()
    }

    override fun readChar(): Char {
        return this.buffer.char
    }

    override fun readInt(): Int {
        return this.buffer.int
    }

    override fun readVarInt(): Int {
        var value = 0
        var size = 0
        var byte: Byte = this.readByte()

        while ((byte and 0x80.toByte()) == 0x80.toByte()) {
            value = value or ((byte and 0x7F.toByte()).toInt() shl (size++ * 7))
            if (size > 5) {
                IOException("VarInt length is greater than 5")
            } else {
                byte = this.readByte()
            }
        }
        return value or ((byte and 0x7F).toInt() shl (size * 7))
    }

    override fun readLong(): Long {
        return this.buffer.long
    }

    override fun readVarLong(): Long {
        var value = 0L
        var size = 0
        var byte: Byte = this.readByte()

        while ((byte and 0x80.toByte()) == 0x80.toByte()) {
            value = value or ((byte and 0x7F.toByte()).toLong() shl (size++ * 7))
            if (size > 10) {
                IOException("VarInt length is greater than 10")
            } else {
                byte = this.readByte()
            }
        }
        return (value or ((byte and 0x7F).toLong() shl (size * 7)))
    }

    override fun readFloat(): Float {
        return this.buffer.float
    }

    override fun readDouble(): Double {
        return this.buffer.double
    }

    override fun readBytes(length: Int): ByteArray {
        if (length < 0) {
            IllegalArgumentException("Array length can't be less than 0")
        }
        val bytes = ByteArray(length)
        this.buffer.get(bytes)
        return bytes
    }

    override fun readBytes(bytes: ByteArray): Int {
        return this.readBytes(bytes, 0, bytes.size)
    }

    override fun readBytes(bytes: ByteArray, offset: Int, length: Int): Int {
        val remaining = this.buffer.remaining()
        var l: Int = length
        if (remaining <= 0) {
            return -1
        } else if (remaining < l) {
            l = remaining
        }

        this.buffer.get(bytes, offset, l)
        return l
    }

    override fun readShorts(length: Int): ShortArray {
        if (length < 0) {
            IllegalArgumentException("Array length can't be less than 0")
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
        val remaining: Int = this.buffer.remaining()
        var l: Int = length
        if (remaining <= 0) {
            return -1
        } else if (remaining < l * 2) {
            l = remaining / 2
        }

        for (i in offset until (length + offset)) {
            shorts[i] = this.readShort()
        }
        return l
    }

    override fun readInts(length: Int): IntArray {
        if (length < 0) {
            IllegalArgumentException("Array length can't be less than 0")
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
        val remaining = this.buffer.remaining()
        var l = length
        if (remaining <= 0) {
            return -1
        } else if (remaining < l * 4) {
            l = remaining / 4
        }
        for (i in offset until (offset + l)) {
            ints[i] = this.readInt()
        }

        return length
    }

    override fun readLongs(length: Int): LongArray {
        if (length < 0) {
            IllegalArgumentException("Array size can't be less than 0")
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
        val remaining = this.buffer.remaining()
        var l = length
        if (remaining <= 0) {
            return -1
        } else if (remaining < l * 2) {
            l = remaining / 2
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
        return this.buffer.remaining()
    }
}