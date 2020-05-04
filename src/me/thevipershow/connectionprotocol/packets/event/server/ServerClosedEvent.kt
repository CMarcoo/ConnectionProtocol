package me.thevipershow.connectionprotocol.packets.event.server

import me.thevipershow.connectionprotocol.packets.Server

class ServerClosedEvent(val server: Server) : ServerEvent {
    override fun call(listener: ServerListener) {
        listener.serverClosed(this)
    }
}