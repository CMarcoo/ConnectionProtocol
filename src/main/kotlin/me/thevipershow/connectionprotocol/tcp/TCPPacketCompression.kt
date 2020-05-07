/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.tcp

import io.netty.handler.codec.ByteToMessageCodec
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import me.thevipershow.connectionprotocol.Session
import me.thevipershow.connectionprotocol.tcp.io.ByteBufNetIn
import me.thevipershow.connectionprotocol.tcp.io.ByteBufNetOut
import java.util.zip.Deflater
import java.util.zip.Inflater

class TCPPacketCompression(val session: Session) : ByteToMessageCodec<ByteBuf>() {
    private companion object SIZE {
        val MAX_COMPRESSED_SIZE = 2097152
    }

    private val deflater = Deflater()
    private val inflater = Inflater()
    private val buffer = ByteArray(8192)

    override fun encode(ctx: ChannelHandlerContext?, inp: ByteBuf, out: ByteBuf) {
        val readable = inp.readableBytes()
        val output = ByteBufNetOut(out)
        if (readable < this.session.getCompressionThreshold()) {
            output.writeVarInt(0)
            out.writeBytes(inp)
        } else {
            val bytes = ByteArray(readable)
            inp.readBytes(bytes)
            output.writeVarInt(bytes.size)
            this.deflater.setInput(bytes, 0, readable)
            this.deflater.finish()
            while (!this.deflater.finished()) {
                val length = this.deflater.deflate(this.buffer)
                output.writeBytes(this.buffer, length)
            }

            this.deflater.reset()
        }
    }

    override fun decode(ctx: ChannelHandlerContext?, buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() != 0) {
            val inp = ByteBufNetIn(buf)
            val size = inp.readVarInt()
            if (size == 0) {
                out.add(buf.readBytes(buf.readableBytes()))
            } else {
                if (size < this.session.getCompressionThreshold()) {
                    throw DecoderException("Badly compressed packet: size of $size is below threshold of ${this.session.getCompressionThreshold()}")
                }
                if (size > MAX_COMPRESSED_SIZE) {
                    throw DecoderException("Badly compressed packet: size of $size is larger than protocol standard maximum $MAX_COMPRESSED_SIZE")
                }
                val bytes = ByteArray(buf.readableBytes())
                inp.readBytes(bytes)
                this.inflater.setInput(bytes)
                val inflated = ByteArray(size)
                this.inflater.inflate(inflated)
                out.add(Unpooled.wrappedBuffer(inflated))
                this.inflater.reset()
            }
        }
    }
}