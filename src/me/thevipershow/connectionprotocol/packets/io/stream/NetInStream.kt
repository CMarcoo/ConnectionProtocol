package me.thevipershow.connectionprotocol.packets.io.stream

import packets.io.NetIn
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.experimental.and

class NetInStream(inputStream: InputStream) : NetIn {
    private var inputStream = inputStream;


    @Throws(IOException::class)
    override fun readBoolean(): Boolean {
        return readByte().compareTo(1) == 0
    }

    @Throws(IOException::class)
    override fun readByte(): Byte {
        return readUnsignedByte().toByte();
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
        var value: Int = 0;
        var size: Int = 0;
        var byte: Byte;
        do {
            byte = this.readByte();
            value = value or ((byte and 0x7F.toByte()).toInt() shl (size++ * 7))
            if (size > 5) {
                IOException("VarInt length is greater than 5")
            }
        } while ((byte and 0x80.toByte()) == 0x80.toByte())
        return value or ((byte and 0x7F).toInt() shl (size * 7))
    }

    @Throws(IOException::class)
    override fun readLong(): Long {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readVarLong(): Long {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readFloat(): Float {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readDouble(): Double {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readBytes(length: Int): ByteArray {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readBytes(bytes: ByteArray): Int {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readBytes(bytes: ByteArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readShorts(length: Int): ShortArray {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readShorts(shorts: ShortArray): Int {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readShorts(shorts: ShortArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readInts(length: Int): IntArray {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readInts(ints: IntArray): Int {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readInts(ints: IntArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readLongs(length: Int): LongArray {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readLongs(longs: LongArray): LongArray {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readLongs(longs: LongArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readString(): String {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun readUUID(): UUID {
        TODO("Not yet implemented")
    }

    @Throws(IOException::class)
    override fun available(): Int {
        TODO("Not yet implemented")
    }
}