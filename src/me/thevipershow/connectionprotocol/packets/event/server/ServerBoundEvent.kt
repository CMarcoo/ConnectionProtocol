package me.thevipershow.connectionprotocol.packets.event.server

import me.thevipershow.connectionprotocol.packets.Server

class ServerBoundEvent(val server: Server) : ServerEvent {
    override fun call(listener: ServerListener) {
        listener.serverBound(this)
    }
}