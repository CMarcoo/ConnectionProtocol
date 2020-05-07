// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol

import me.thevipershow.connectionprotocol.event.server.*
import me.thevipershow.connectionprotocol.packet.PacketProtocol
import java.lang.reflect.Constructor

class Server(host: String, port: Int, protocol: Class<out PacketProtocol>, factory: SessionFactory) {
    private lateinit var listener: ConnectionListener
    private var sessions = ArrayList<Session>()
    private var flags = HashMap<String, Any>()
    private var listeners = ArrayList<ServerListener>()

    private var host = host
    private var port = port
    private var protocol = protocol
    private var factory = factory

    fun bind(): Server {
        return this.bind(true)
    }

    fun bind(wait: Boolean): Server {
        this.listener = this.factory.createServerListener(this)
        this.listener.bind(wait, Runnable {
            run {
                callEvent(ServerBoundEvent(this@Server))
            }
        })
        return this
    }

    fun getHost(): String {
        return this.host
    }

    fun getPort(): Int {
        return this.port
    }

    fun getPacketProtocol(): Class<out PacketProtocol> {
        return this.protocol
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
        return HashMap<String, Any>(this.flags)
    }

    fun hasGlobalFlag(key: String): Boolean {
        return this.flags.containsKey(key)
    }

    fun <T> getGlobalFlag(key: String): T? {
        val value: Any? = this.flags[key] ?: return null
        try {
            return (value as T)
        } catch (exc: ClassCastException) {
            throw IllegalStateException("Tried to get flag $key as the wrong type. Actual type: ${value!!.javaClass.simpleName}")
        }
    }

    fun setGlobalFlag(key: String, value: Any) {
        this.flags[key] = value
    }

    fun getListeners(): List<ServerListener> {
        return ArrayList<ServerListener>(this.listeners)
    }

    fun addListener(listener: ServerListener) {
        this.listeners.add(listener)
    }

    fun removeListener(listener: ServerListener) {
        this.listeners.remove(listener)
    }

    fun callEvent(event: ServerEvent) {
        this.listeners.forEach { event.call(it) }
    }

    fun getSessions(): List<Session> {
        return ArrayList<Session>(this.sessions)
    }

    fun addSession(session: Session) {
        this.sessions.add(session)
        this.callEvent(
            SessionAddedEvent(
                this,
                session
            )
        )
    }

    fun removeSession(session: Session) {
        this.sessions.remove(session)
        if (session.isConnected()) {
            session.disconnect("Connection closed")
        }

        this.callEvent(
            SessionRemovedEvent(
                this,
                session
            )
        )
    }

    fun close() {
        this.close(true)
    }

    fun close(wait: Boolean) {
        this.callEvent(ServerClosingEvent(this))
        this.getSessions().forEach {
            if (it.isConnected()) {
                it.disconnect("Server closed")
            }
        }
        this.listener.close(wait, Runnable {
            run {
                callEvent(ServerClosedEvent(this@Server))
            }
        })
    }
}