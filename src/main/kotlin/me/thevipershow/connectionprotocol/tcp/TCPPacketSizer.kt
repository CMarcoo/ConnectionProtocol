/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.tcp

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import io.netty.handler.codec.CorruptedFrameException
import me.thevipershow.connectionprotocol.Session
import me.thevipershow.connectionprotocol.tcp.io.ByteBufNetIn
import me.thevipershow.connectionprotocol.tcp.io.ByteBufNetOut

class TCPPacketSizer(private val session: Session) : ByteToMessageCodec<ByteBuf>() {
    override fun encode(p0: ChannelHandlerContext?, inp: ByteBuf, output: ByteBuf) {
        val length = inp.readableBytes()
        output.ensureWritable(this.session.getPacketProtocol().getPacketHeader().getLengthSize(length) + length)
        this.session.getPacketProtocol().getPacketHeader().writeLength(
            ByteBufNetOut(
                output
            ), length)
        output.writeBytes(inp)
    }

    override fun decode(ctx: ChannelHandlerContext?, buf: ByteBuf, out: MutableList<Any>) {
        val size = this.session.getPacketProtocol().getPacketHeader().getLengthSize()
        if (size > 0) {
            buf.markReaderIndex()
            val lengthBytes = ByteArray(size)
            for (i in lengthBytes.indices) {
                if (!buf.isReadable) {
                    buf.resetReaderIndex()
                    return
                }

                lengthBytes[i] = buf.readByte()
                if ((this.session.getPacketProtocol().getPacketHeader().isLengthVariable() && lengthBytes[i] >= 0) || i == (size - 1)) {
                      val length = this.session.getPacketProtocol().getPacketHeader().readLength(
                          ByteBufNetIn(
                              Unpooled.wrappedBuffer(lengthBytes)
                          ), buf.readableBytes())
                      if (buf.readableBytes() < length) {
                          buf.resetReaderIndex()
                          return
                      }

                    out.add(buf.readBytes(length))
                    return
                }
            }
            throw CorruptedFrameException("Length is too long")
        } else {
            out.add(buf.readBytes(buf.readableBytes()))
        }
    }
}