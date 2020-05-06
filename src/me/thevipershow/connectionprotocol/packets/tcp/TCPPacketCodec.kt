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
import me.thevipershow.connectionprotocol.packets.event.session.PacketReceivedEvent
import me.thevipershow.connectionprotocol.packets.packet.Packet
import me.thevipershow.connectionprotocol.packets.tcp.io.ByteBufNetIn
import me.thevipershow.connectionprotocol.packets.tcp.io.ByteBufNetOut

class TCPPacketCodec(private val session: Session) : ByteToMessageCodec<Packet>() {
    override fun encode(p0: ChannelHandlerContext?, packet: Packet, buf: ByteBuf) {
        val out = ByteBufNetOut(buf)
        this.session.getPacketProtocol().getPacketHeader()
            .writePacketID(out, this.session.getPacketProtocol().getOutgoingID(packet.javaClass))
        packet.write(out)
    }

    override fun decode(ctx: ChannelHandlerContext?, buf: ByteBuf, output: MutableList<Any>) {
        val initial = buf.readerIndex()
        val input = ByteBufNetIn(buf)
        val id = this.session.getPacketProtocol().getPacketHeader().readPacketID(input)
        if (id == -1) {
            buf.readerIndex(initial)
            return
        }
        val packet = this.session.getPacketProtocol().createIncomingPacket(id)
        packet.read(input)

        if (buf.readableBytes() > 0) {
            throw IllegalStateException("Packet [${packet::class.simpleName}] not fully read")
        }
        if (packet.isPriority()) {
            this.session.callEvent(PacketReceivedEvent(this.session, packet))
        }
        output.add(packet)
    }
}