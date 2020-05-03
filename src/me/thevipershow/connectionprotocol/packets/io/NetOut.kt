package packets.io

import java.io.IOException
import java.util.UUID;

interface NetOut {
    @Throws(IOException::class)
    fun writeBoolean(boolean: Boolean)

    @Throws(IOException::class)
    fun writeByte(byte: Int)

    @Throws(IOException::class)
    fun writeShort(short: Int)

    @Throws(IOException::class)
    fun writeChar(char: Int)

    @Throws(IOException::class)
    fun writeInt(int: Int)

    @Throws(IOException::class)
    fun writeVarInt(int: Int)

    @Throws(IOException::class)
    fun writeLong(long: Long)

    @Throws(IOException::class)
    fun writeVarLong(varLong: Long)

    @Throws(IOException::class)
    fun writeFloat(float: Float)

    @Throws(IOException::class)
    fun writeDouble(double: Double)

    @Throws(IOException::class)
    fun writeBytes(byteArray: ByteArray)

    @Throws(IOException::class)
    fun writeBytes(byteArray: ByteArray, length: Int)

    @Throws(IOException::class)
    fun writeShorts(shortArray: ShortArray)

    @Throws(IOException::class)
    fun writeShorts(shortArray: ShortArray, length: Int)

    @Throws(IOException::class)
    fun writeInts(intArray: IntArray)

    @Throws(IOException::class)
    fun writeInts(intArray: IntArray, length: Int)

    @Throws(IOException::class)
    fun writeLongs(longArray: LongArray)

    @Throws(IOException::class)
    fun writeLongs(longArray: LongArray, length: Int)

    @Throws(IOException::class)
    fun writeString(string: String)

    @Throws(IOException::class)
    fun writeUUID(uuid: UUID)

    @Throws(IOException::class)
    fun flush()
}