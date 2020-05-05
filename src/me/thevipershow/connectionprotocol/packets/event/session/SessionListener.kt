// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.packets.event.session

interface SessionListener {
    /**
     * Called when a session receives a packet.
     *
     * @param event Data relating to the event.
     */
    fun packetReceived(event: PacketReceivedEvent)

    /**
     * Called when a session is sending a packet.
     *
     * @param event Data relating to the event.
     */
    fun packetSending(event: PacketSendingEvent)

    /**
     * Called when a session sends a packet.
     *
     * @param event Data relating to the event.
     */
    fun packetSent(event: PacketSentEvent)

    /**
     * Called when a session connects.
     *
     * @param event Data relating to the event.
     */
    fun connected(event: ConnectedEvent)

    /**
     * Called when a session is about to disconnect.
     *
     * @param event Data relating to the event.
     */
    fun disconnecting(event: DisconnectingEvent)

    /**
     * Called when a session is disconnected.
     *
     * @param event Data relating to the event.
     */
    fun disconnected(event: DisconnectedEvent)
}