package me.thevipershow.connectionprotocol.packets

interface SessionFactory {

    fun createClientSession(client: Client): Session

    fun createServerListener(server: Server): ConnectionListener
}