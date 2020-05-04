package me.thevipershow.connectionprotocol.packets

interface ConnectionListener {

    fun getHost():String

    fun getPort() : Int

    fun isListening(): Boolean

    fun bind()

    fun bind(wait: Boolean, callback: Runnable)

    fun close()

    fun close(wait: Boolean)

    fun close(wait: Boolean, callback: Runnable)
}