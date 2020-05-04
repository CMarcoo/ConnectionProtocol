package me.thevipershow.connectionprotocol.packets.event.session

class SessionAdapter: SessionListener {
    override fun packetReceived(event: PacketReceivedEvent) {
        TODO("Not yet implemented")
    }

    override fun packetSending(event: PacketSendingEvent) {
        TODO("Not yet implemented")
    }

    override fun packetSent(event: PacketSentEvent) {
        TODO("Not yet implemented")
    }

    override fun connected(event: ConnectedEvent) {
        TODO("Not yet implemented")
    }

    override fun disconnecting(event: DisconnectingEvent) {
        TODO("Not yet implemented")
    }

    override fun disconnected(event: DisconnectedEvent) {
        TODO("Not yet implemented")
    }
}