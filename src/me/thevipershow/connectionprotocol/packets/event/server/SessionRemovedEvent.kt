package me.thevipershow.connectionprotocol.packets.event.server

import me.thevipershow.connectionprotocol.packets.Server

class SessionRemovedEvent(val server: Server): ServerEvent {
    override fun call(listener: ServerListener) {
        listener.sessionRemoved(this)
    }
}