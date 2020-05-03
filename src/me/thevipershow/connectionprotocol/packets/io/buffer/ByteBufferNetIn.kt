package me.thevipershow.connectionprotocol.packets.io.buffer

import me.thevipershow.connectionprotocol.packets.io.NetIn
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

class ByteBufferNetIn(var buffer: ByteBuffer): NetIn {
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
        TODO("Not yet implemented")
    }

    override fun readInt(): Int {
        TODO("Not yet implemented")
    }

    override fun readVarInt(): Int {
        TODO("Not yet implemented")
    }

    override fun readLong(): Long {
        TODO("Not yet implemented")
    }

    override fun readVarLong(): Long {
        TODO("Not yet implemented")
    }

    override fun readFloat(): Float {
        TODO("Not yet implemented")
    }

    override fun readDouble(): Double {
        TODO("Not yet implemented")
    }

    override fun readBytes(length: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun readBytes(bytes: ByteArray): Int {
        TODO("Not yet implemented")
    }

    override fun readBytes(bytes: ByteArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override fun readShorts(length: Int): ShortArray {
        TODO("Not yet implemented")
    }

    override fun readShorts(shorts: ShortArray): Int {
        TODO("Not yet implemented")
    }

    override fun readShorts(shorts: ShortArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override fun readInts(length: Int): IntArray {
        TODO("Not yet implemented")
    }

    override fun readInts(ints: IntArray): Int {
        TODO("Not yet implemented")
    }

    override fun readInts(ints: IntArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override fun readLongs(length: Int): LongArray {
        TODO("Not yet implemented")
    }

    override fun readLongs(longs: LongArray): Int {
        TODO("Not yet implemented")
    }

    override fun readLongs(longs: LongArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override fun readString(): String {
        TODO("Not yet implemented")
    }

    override fun readUUID(): UUID {
        TODO("Not yet implemented")
    }

    override fun available(): Int {
        TODO("Not yet implemented")
    }

}