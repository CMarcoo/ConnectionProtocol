// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.packets.packet

import me.thevipershow.connectionprotocol.packets.Client
import me.thevipershow.connectionprotocol.packets.Server
import me.thevipershow.connectionprotocol.packets.Session
import me.thevipershow.connectionprotocol.packets.crypt.PacketEncryption
import java.lang.Exception

abstract class PacketProtocol {
    private val incoming = HashMap<Int, Class<out Packet>>()
    private val outgoing = HashMap<Class<out Packet>, Int>()

    abstract fun getSRVRecordPrefix(): String

    abstract fun getPacketHeader(): PacketHeader

    abstract fun getEncryption(): PacketEncryption?

    abstract fun newClientSession(client: Client, session: Session)

    abstract fun newServerSession(server: Server, session: Session)

    fun clearPackets() {
        this.incoming.clear()
        this.outgoing.clear()
    }

    @Throws(IllegalArgumentException::class)
    fun register(ID: Int, packet: Class<out Packet>) {
        this.registerIncoming(ID, packet)
        this.registerOutgoing(ID, packet)
    }

    @Throws(IllegalArgumentException::class)
    fun registerIncoming(ID: Int, packet: Class<out Packet>) {
        this.incoming[ID] = packet
        try {
            this.createIncomingPacket(ID)
        } catch (state: IllegalStateException) {
            this.incoming.remove(ID)
            throw IllegalArgumentException(state.message, state.cause)
        }
    }

    fun registerOutgoing(ID: Int, packet: Class<out Packet>) {
        this.outgoing[packet] = ID
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun createIncomingPacket(ID: Int): Packet {
        if (ID < 0 || !this.incoming.containsKey(ID) || this.incoming[ID] == null) {
            throw IllegalArgumentException("Invalid packet ID: $ID")
        }
        val packet = this.incoming[ID]!!
        try {
            val constructor = packet.getDeclaredConstructor()
            if (!constructor.canAccess(packet)) {
                constructor.trySetAccessible()
            }
            return constructor.newInstance()
        } catch (noSuchMethod: NoSuchMethodError) {
            throw IllegalStateException("Packet [$ID, ${packet.name}] doesn't have a constructor without parameters")
        } catch (exc: Exception) {
            throw IllegalStateException("Packet [$ID, ${packet.name}] couldn't be instantiated")
        }
    }

    @Throws(IllegalArgumentException::class)
    fun getOutgoingID(packet: Class<out Packet>): Int {
        if (!this.outgoing.containsKey(packet) || this.outgoing[packet] == null) {
            throw IllegalArgumentException("Unregistered outgoing packet class: ${packet.name}")
        }
        return this.outgoing[packet]!!
    }
}