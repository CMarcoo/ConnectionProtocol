package me.thevipershow.connectionprotocol.packets.io.stream

import me.thevipershow.connectionprotocol.packets.io.NetIn
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.util.UUID;
import kotlin.experimental.and

class NetInStream(var inputStream: InputStream) : NetIn {
    @Throws(IOException::class)
    override fun readBoolean(): Boolean {
        return readByte().compareTo(1) == 0
    }

    @Throws(IOException::class)
    override fun readByte(): Byte {
        return readUnsignedByte().toByte()
    }

    @Throws(IOException::class)
    override fun readUnsignedByte(): Int {
        val read = this.inputStream.read()
        return if (read > 0) read else throw EOFException()
    }

    @Throws(IOException::class)
    override fun readShort(): Short {
        return readUnsignedShort().toShort()
    }

    @Throws(IOException::class)
    override fun readUnsignedShort(): Int {
        val v1 = this.readUnsignedByte()
        val v2 = this.readUnsignedByte()
        return ((v1 shl 8) + (v2 shl 0))
    }

    @Throws(IOException::class)
    override fun readChar(): Char {
        val v1 = this.readUnsignedByte()
        val v2 = this.readUnsignedByte()
        return ((v1 shl 8) + (v2 shl 0)).toChar()
    }

    @Throws(IOException::class)
    override fun readInt(): Int {
        val v1 = this.readUnsignedByte()
        val v2 = this.readUnsignedByte()
        val v3 = this.readUnsignedByte()
        val v4 = this.readUnsignedByte()
        return ((v1 shl 24) + (v2 shl 16) + (v3 shl 8) + (v4 shl 0))
    }

    @Throws(IOException::class)
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

    @Throws(IOException::class)
    override fun readLong(): Long {
        val readBytes: ByteArray = this.readBytes(8)
        return ((readBytes[0].toLong() shl 56)
                + ((readBytes[1].toLong() and 255) shl 48)
                + ((readBytes[2].toLong() and 255) shl 40)
                + ((readBytes[3].toLong() and 255) shl 32)
                + ((readBytes[4].toLong() and 255) shl 24)
                + ((readBytes[5].toInt() and 255) shl 16)
                + ((readBytes[6].toInt() and 255) shl 8)
                + ((readBytes[7].toInt() and 255) shl 0))
    }

    @Throws(IOException::class)
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

    @Throws(IOException::class)
    override fun readFloat(): Float {
        return Float.fromBits(this.readInt())
    }

    @Throws(IOException::class)
    override fun readDouble(): Double {
        return Double.fromBits(this.readLong())
    }

    @Throws(IOException::class)
    override fun readBytes(length: Int): ByteArray {
        if (length < 0) {
            IllegalArgumentException("Array size can't be < 0")
        }
        val bytes = ByteArray(length)
        var n = 0;
        while (n < length) {
            val count = this.inputStream.read(bytes, n, length - n)
            if (count < 0) {
                EOFException()
            }
            n += count
        }
        return bytes
    }

    @Throws(IOException::class)
    override fun readBytes(bytes: ByteArray): Int {
        return this.inputStream.read(bytes)
    }

    @Throws(IOException::class)
    override fun readBytes(bytes: ByteArray, offset: Int, length: Int): Int {
        return this.inputStream.read(bytes, offset, length)
    }

    @Throws(IOException::class)
    override fun readShorts(length: Int): ShortArray {
        if (length < 0) {
            IllegalArgumentException("Array size can't be < 0")
        }
        val shorts = ShortArray(length)
        if (this.readShorts(shorts) < length) {
            EOFException()
        }
        return shorts
    }

    @Throws(IOException::class)
    override fun readShorts(shorts: ShortArray): Int {
        return this.readShorts(shorts, 0, shorts.size)
    }

    @Throws(IOException::class)
    override fun readShorts(shorts: ShortArray, offset: Int, length: Int): Int {
        for (i in offset until (offset + length)) {
            try {
                shorts[i] = this.readShort()
            } catch (eofException: EOFException) {
                return i - offset
            }
        }
        return length
    }

    @Throws(IOException::class)
    override fun readInts(length: Int): IntArray {
        if (length < 0) {
            IllegalArgumentException("Array size can't be < 0")
        }
        val ints = IntArray(length)
        if (this.readInts(ints) < length) {
            EOFException()
        }
        return ints
    }

    @Throws(IOException::class)
    override fun readInts(ints: IntArray): Int {
        return this.readInts(ints, 0, ints.size)
    }

    @Throws(IOException::class)
    override fun readInts(ints: IntArray, offset: Int, length: Int): Int {
        for (i in offset until (offset + length)) {
            try {
                ints[i] = this.readInt()
            } catch (eofException: EOFException) {
                return i - offset
            }
        }
        return length
    }

    @Throws(IOException::class)
    override fun readLongs(length: Int): LongArray {
        if (length < 0) {
            IllegalArgumentException("Array size can't be < 0")
        }
        val longs = LongArray(length)
        if (this.readLongs(longs) < length) {
            EOFException()
        }
        return longs
    }

    @Throws(IOException::class)
    override fun readLongs(longs: LongArray): Int {
        return this.readLongs(longs, 0, longs.size)
    }

    @Throws(IOException::class)
    override fun readLongs(longs: LongArray, offset: Int, length: Int): Int {
        for (i in offset until (offset + length)) {
            try {
                longs[i] = this.readLong()
            } catch (eofException: EOFException) {
                return i - offset
            }
        }
        return length
    }

    @Throws(IOException::class)
    override fun readString(): String {
        val bytes = this.readBytes(this.readVarInt())
        return String(bytes, Charsets.UTF_8)
    }

    @Throws(IOException::class)
    override fun readUUID(): UUID {
        return UUID(this.readLong(), this.readLong())
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return this.inputStream.available()
    }
}