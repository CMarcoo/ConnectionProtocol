package me.thevipershow.connectionprotocol.packets.event.server

interface ServerListener {
    fun serverBound(event: ServerBoundEvent)

    fun serverClosing(event: ServerClosingEvent)

    fun serverClosed(event: ServerClosedEvent)

    fun sessionAdded(event: SessionAddedEvent)

    fun sessionRemoved(event: SessionRemovedEvent)
}