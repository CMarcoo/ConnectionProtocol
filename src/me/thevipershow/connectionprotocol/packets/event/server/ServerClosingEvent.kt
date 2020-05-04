package me.thevipershow.connectionprotocol.packets.event.server

import me.thevipershow.connectionprotocol.packets.Server

class ServerClosingEvent(val server: Server): ServerEvent {
    override fun call(listener: ServerListener) {
        listener.serverClosing(this)
    }
}