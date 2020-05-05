/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package me.thevipershow.connectionprotocol.packets

import java.net.SocketAddress

class ProxyInfo(
    private val type: Type,
    private val address: SocketAddress,
    private val username: String? = null,
    private val password: String? = null,
    private val authenticated: Boolean = (username != null && password != null)
) {
    enum class Type {
        HTTP, SOCKS4, SOCKS5
    }

    fun getType(): Type {
        return this.type
    }

    fun getAddress(): SocketAddress {
        return this.address
    }

    fun isAuthenticated(): Boolean {
        return this.authenticated
    }

    fun getUsername(): String? {
        return this.username
    }

    fun getPassword(): String? {
        return this.password
    }
}