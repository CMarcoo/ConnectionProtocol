package me.thevipershow.connectionprotocol.packets.event.session

interface SessionEvent {
    fun call(listener: SessionListener)
}