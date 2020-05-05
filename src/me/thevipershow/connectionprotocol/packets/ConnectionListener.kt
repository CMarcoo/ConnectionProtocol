// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
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