package me.thevipershow.connectionprotocol.packets.event.session

class SessionAdapter : SessionListener {
    override fun packetReceived(event: PacketReceivedEvent) {}

    override fun packetSending(event: PacketSendingEvent) {}

    override fun packetSent(event: PacketSentEvent) {}

    override fun connected(event: ConnectedEvent) {}

    override fun disconnecting(event: DisconnectingEvent) {}

    override fun disconnected(event: DisconnectedEvent) {}
}