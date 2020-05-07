// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.event.session

import me.thevipershow.connectionprotocol.Session
import me.thevipershow.connectionprotocol.packet.Packet
import kotlin.ClassCastException

class PacketSentEvent(val session: Session, private val packet: Packet) :
    SessionEvent {
    fun <T : Packet> getPacket(): T {
        try {
            return (this.packet as T)
        } catch (exc : ClassCastException) {
            throw IllegalStateException("Tried to get packet as the wrong type. Actual type: ${this.packet::class.qualifiedName}")
        }
    }

    override fun call(listener: SessionListener) {
        listener.packetSent(this)
    }
}