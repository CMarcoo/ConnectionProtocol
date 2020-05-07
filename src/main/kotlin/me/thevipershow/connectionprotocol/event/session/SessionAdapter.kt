// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.event.session

class SessionAdapter : SessionListener {
    override fun packetReceived(event: PacketReceivedEvent) {}

    override fun packetSending(event: PacketSendingEvent) {}

    override fun packetSent(event: PacketSentEvent) {}

    override fun connected(event: ConnectedEvent) {}

    override fun disconnecting(event: DisconnectingEvent) {}

    override fun disconnected(event: DisconnectedEvent) {}
}