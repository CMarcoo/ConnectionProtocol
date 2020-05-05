/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.packets.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import me.thevipershow.connectionprotocol.packets.Session

class TCPPacketEncryptor(private val session: Session) : ByteToMessageCodec<ByteBuf>() {
    private var decryptedArray = ByteArray(0)
    private var encryptedArray = ByteArray(0)

    override fun encode(ctx: ChannelHandlerContext, input: ByteBuf, out: ByteBuf) {
        if (this.session.getPacketProtocol().getEncryption() != null) {
            val length = input.readableBytes()
            val bytes = this.getBytes(input)
            val outLength = this.session.getPacketProtocol().getEncryption()!!.getEncryptOutputSize(length)
            if (this.encryptedArray.size < outLength) {
                this.encryptedArray = ByteArray(outLength)
            }

            out.writeBytes(
                this.encryptedArray, 0, this.session.getPacketProtocol().getEncryption()!!
                    .encrypt(bytes, 0, length, this.encryptedArray, 0)
            )
        } else {
            out.writeBytes(input)
        }
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (this.session.getPacketProtocol().getEncryption() != null) {
            val length = buf.readableBytes()
            val bytes = this.getBytes(buf)
            val result =
                ctx.alloc().heapBuffer(this.session.getPacketProtocol().getEncryption()!!.getDecryptOutputSize(length))
            result.writerIndex(
                this.session.getPacketProtocol().getEncryption()!!
                    .decrypt(bytes, 0, length, result.array(), result.arrayOffset())
            )

        } else {
            out.add(buf.readBytes(buf.readableBytes()))
        }
    }

    private fun getBytes(buf: ByteBuf): ByteArray {
        val length = buf.readableBytes()
        if (this.decryptedArray.size < length) {
            this.decryptedArray = ByteArray(length)
        }
        buf.readBytes(this.decryptedArray, 0, length)
        return this.decryptedArray
    }
}