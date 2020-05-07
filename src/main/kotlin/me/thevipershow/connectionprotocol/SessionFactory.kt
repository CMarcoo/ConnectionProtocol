package me.thevipershow.connectionprotocol
// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
interface SessionFactory {

    fun createClientSession(client: Client): Session

    fun createServerListener(server: Server): ConnectionListener
}