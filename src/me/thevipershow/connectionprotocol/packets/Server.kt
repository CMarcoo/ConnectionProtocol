// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.packets

import me.thevipershow.connectionprotocol.packets.event.server.ServerListener
import me.thevipershow.connectionprotocol.packets.packet.PacketProtocol
import java.lang.reflect.Constructor

class Server(val host: String, val port: Int, val protocol: Class<out PacketProtocol>, val factory: SessionFactory) {
    private lateinit var listener: ConnectionListener
    private var sessions = ArrayList<ServerListener>()
    private var flags = HashMap<String, Any>()
    private var listeners = ArrayList<ServerListener>()


    fun bind(): Server {
        return this.bind(true)
    }

    fun bind(wait: Boolean): Server {
        this.listener = this.factory.createServerListener(this)
        this.listener.bind(wait, Runnable { })
    }

    @Throws(IllegalStateException::class)
    fun createPacketProtocol(): PacketProtocol {
        try {
            val constructor: Constructor<out PacketProtocol> = this.protocol.getDeclaredConstructor()
            if (!constructor.isAccessible) {
                constructor.trySetAccessible()
            }
            return constructor.newInstance()
        } catch (e: NoSuchMethodError) {
            throw IllegalStateException("PacketProtocol [${protocol.name}] does not have a no-parameter constructor")
        } catch (e: Exception) {
            throw IllegalStateException("Failed to instantiate PacketProtocol ${protocol.name}", e)
        }
    }

    fun getGlobalFlags(): Map<String, Any> {
        return HashMap<String,Any>(this.flags)
    }

    fun hasGlobalFlag(key:String): Boolean {
        return this.flags.containsKey(key)
    }

    fun <T> getGlobalFlag(key: String): T? {
        val value: Any? = this.flags[key] ?: return null
        try {
            return (value as T)
        } catch (exc: ClassCastException) {
            throw IllegalStateException("Tried to get flag $key as the wrong type. Actual type: ${value::class.qualifiedName}")
        }
    }

    fun setGlobalFlag(key:String, value:Any) {
        this.flags[key] = value
    }

    fun getListeners(): List<ServerListener>
}