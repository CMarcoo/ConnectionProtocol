// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.packets.event.session

import me.thevipershow.connectionprotocol.packets.Session
import me.thevipershow.connectionprotocol.packets.packet.Packet

class PacketReceivedEvent(val session: Session, private var packet: Packet) : SessionEvent {
    fun <T : Packet> getPacket(): T {
        try {
            return (packet as T)
        } catch (exc: ClassCastException) {
            throw IllegalStateException("Tried to get packet as the wrong type. Actual type: ${this.packet::class.qualifiedName}")
        }
    }

    fun setPacket(packet: Packet) {
        this.packet = packet
    }

    override fun call(listener: SessionListener) {
        listener.packetReceived(this)
    }
}