package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.comm.Command

// currently a placeholder for all remote communicators to see how it fits into the design
class RestClientConnector extends ClientConnector {

    int getPoolSize() { 10 }

    List<Command> transmit(Command command) {

    }

}
