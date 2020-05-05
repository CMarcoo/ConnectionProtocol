/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.packets.tcp

import io.netty.handler.codec.ByteToMessageCodec
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import me.thevipershow.connectionprotocol.packets.Session
import me.thevipershow.connectionprotocol.packets.io.buffer.ByteBufferNetOut
import java.util.zip.Deflater
import java.util.zip.Inflater

class TCPPacketCompression(val session: Session): ByteToMessageCodec<ByteBuf>() {
    companion object SIZE private val MAX_COMPRESSED_SIZE = 2097152

    private val deflater = Deflater()
    private val inflater = Inflater()
    private val buffer = ByteArray(8192)

    override fun encode(ctx: ChannelHandlerContext?, inp: ByteBuf?, out: ByteBuf?) {
        val readable = inp?.readableBytes()
        val output =
    }

    override fun decode(p0: ChannelHandlerContext?, p1: ByteBuf?, p2: MutableList<Any>?) {
        TODO("Not yet implemented")
    }
}