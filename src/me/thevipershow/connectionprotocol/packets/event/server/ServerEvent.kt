package me.thevipershow.connectionprotocol.packets.event.server

interface ServerEvent {
    fun call(listener: ServerListener)
}