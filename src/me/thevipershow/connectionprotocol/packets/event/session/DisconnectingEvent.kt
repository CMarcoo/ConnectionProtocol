// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.packets.event.session

import me.thevipershow.connectionprotocol.packets.Session

class DisconnectingEvent(val reason: String?, val session: Session, val cause: Throwable? = null) : SessionEvent {
    override fun call(listener: SessionListener) {
        listener.disconnecting(this)
    }
}