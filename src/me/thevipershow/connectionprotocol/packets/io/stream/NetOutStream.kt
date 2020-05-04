package me.thevipershow.connectionprotocol.packets.io.stream

import me.thevipershow.connectionprotocol.packets.io.NetOut
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class NetOutStream(var outputStream: OutputStream) : NetOut {
    @Throws(IOException::class)
    override fun writeBoolean(boolean: Boolean) {
        this.writeByte(if (boolean) 1 else 0)
    }

    @Throws(IOException::class)
    override fun writeByte(byte: Int) {
        this.outputStream.write(byte)
    }

    @Throws(IOException::class)
    override fun writeShort(short: Int) {
        this.writeByte((short ushr 8) and 0xFF)
        this.writeByte((short ushr 0) and 0xFF)
    }

    @Throws(IOException::class)
    override fun writeChar(char: Int) {
        this.writeByte((char ushr 8) and 0xFF)
        this.writeByte((char ushr 8) and 0xFF)
    }

    @Throws(IOException::class)
    override fun writeInt(int: Int) {
        for (i in 24 downTo 0 step 8) {
            this.writeByte((int ushr i) and 0xFF)
        }
    }

    @Throws(IOException::class)
    override fun writeVarInt(int: Int) {
        var i = int;
        while ((i and 0x7F.inv()) != 0) {
            this.writeByte((i and 0x7F) or 0x80)
            i = i ushr 7
        }

        this.writeByte(i)
    }

    @Throws(IOException::class)
    override fun writeLong(long: Long) {
        for (i in 56 downTo 0 step 8) {
            this.writeByte((long ushr i).toInt())
        }
    }

    @Throws(IOException::class)
    override fun writeVarLong(varLong: Long) {
        var l = varLong
        while ((l and 0x7F.inv()) != 0L) {
            this.writeByte(((l and 0x7F) or 0x80).toInt())
            l = l ushr 7
        }
        this.writeByte(l.toInt())
    }

    @Throws(IOException::class)
    override fun writeFloat(float: Float) {
        this.writeInt(float.toBits())
    }

    @Throws(IOException::class)
    override fun writeDouble(double: Double) {
        this.writeLong(double.toBits())
    }

    @Throws(IOException::class)
    override fun writeBytes(byteArray: ByteArray) {
        this.writeBytes(byteArray, byteArray.size)
    }

    @Throws(IOException::class)
    override fun writeBytes(byteArray: ByteArray, length: Int) {
        this.outputStream.write(byteArray, 0, length)
    }

    @Throws(IOException::class)
    override fun writeShorts(shortArray: ShortArray) {
        this.writeShorts(shortArray, shortArray.size)
    }

    @Throws(IOException::class)
    override fun writeShorts(shortArray: ShortArray, length: Int) {
        for (i in 0 until length) {
            this.writeShort(shortArray[i].toInt())
        }
    }

    @Throws(IOException::class)
    override fun writeInts(intArray: IntArray) {
        this.writeInts(intArray, intArray.size)
    }

    @Throws(IOException::class)
    override fun writeInts(intArray: IntArray, length: Int) {
        for (i in 0 until length) {
            this.writeInt(intArray[i])
        }
    }

    @Throws(IOException::class)
    override fun writeLongs(longArray: LongArray) {
        this.writeLongs(longArray, longArray.size)
    }

    @Throws(IOException::class)
    override fun writeLongs(longArray: LongArray, length: Int) {
        for (i in 0 until length) {
            this.writeLong(longArray[i])
        }
    }

    @Throws(IOException::class)
    override fun writeString(string: String) {
        val bytes = string.toByteArray(Charsets.UTF_8)
        if (bytes.size > Short.MAX_VALUE) {
            IOException("String is too big ( ${bytes.size} bytes, max ${Short.MAX_VALUE} )")
        } else {
            this.writeVarInt(bytes.size)
            this.writeBytes(bytes)
        }
    }

    @Throws(IOException::class)
    override fun writeUUID(uuid: UUID) {
        this.writeLong(uuid.mostSignificantBits)
        this.writeLong(uuid.leastSignificantBits)
    }

    @Throws(IOException::class)
    override fun flush() {
        this.outputStream.flush()
    }
}