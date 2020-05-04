package me.thevipershow.connectionprotocol.packets.event.session

import me.thevipershow.connectionprotocol.packets.Session

class DisconnectingEvent(val reason: String, val session: Session, val cause: Throwable? = null) : SessionEvent {
    override fun call(listener: SessionListener) {
        listener.disconnecting(this)
    }
}