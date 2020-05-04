package me.thevipershow.connectionprotocol.packets.event.session

import me.thevipershow.connectionprotocol.packets.Session

class DisconnectedEvent(val reason: String, val session: Session, val cause: Throwable? = null) : SessionEvent {
    override fun call(listener: SessionListener) {
        listener.disconnected(this)
    }
}