package me.thevipershow.connectionprotocol.packets.io

import java.io.IOException
import java.util.UUID

interface NetIn {
    @Throws(IOException::class)
    fun readBoolean(): Boolean

    @Throws(IOException::class)
    fun readByte(): Byte

    @Throws(IOException::class)
    fun readUnsignedByte(): Int

    @Throws(IOException::class)
    fun readShort(): Short

    @Throws(IOException::class)
    fun readUnsignedShort(): Int

    @Throws(IOException::class)
    fun readChar(): Char

    @Throws(IOException::class)
    fun readInt(): Int

    @Throws(IOException::class)
    fun readVarInt(): Int

    @Throws(IOException::class)
    fun readLong(): Long

    @Throws(IOException::class)
    fun readVarLong(): Long

    @Throws(IOException::class)
    fun readFloat(): Float

    @Throws(IOException::class)
    fun readDouble(): Double

    @Throws(IOException::class)
    fun readBytes(length: Int): ByteArray

    @Throws(IOException::class)
    fun readBytes(bytes: ByteArray): Int

    @Throws(IOException::class)
    fun readBytes(bytes: ByteArray, offset: Int, length: Int): Int

    @Throws(IOException::class)
    fun readShorts(length: Int): ShortArray

    @Throws(IOException::class)
    fun readShorts(shorts: ShortArray): Int

    @Throws(IOException::class)
    fun readShorts(shorts: ShortArray, offset: Int, length: Int): Int

    @Throws(IOException::class)
    fun readInts(length: Int): IntArray

    @Throws(IOException::class)
    fun readInts(ints: IntArray): Int

    @Throws(IOException::class)
    fun readInts(ints: IntArray, offset: Int, length: Int): Int

    @Throws(IOException::class)
    fun readLongs(length: Int): LongArray

    @Throws(IOException::class)
    fun readLongs(longs: LongArray): Int

    @Throws(IOException::class)
    fun readLongs(longs: LongArray, offset: Int, length: Int): Int

    @Throws(IOException::class)
    fun readString(): String

    @Throws(IOException::class)
    fun readUUID(): UUID

    @Throws(IOException::class)
    fun available(): Int
}