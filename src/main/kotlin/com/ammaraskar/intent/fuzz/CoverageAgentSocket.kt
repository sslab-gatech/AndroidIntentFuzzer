package com.ammaraskar.intent.fuzz

import java.net.Socket

object InstrumentationConstants {
    const val COVERAGE_MAP_SIZE = 64 * 1024
}

/**
 * Connects to and communicates with the socket server created by the coverage agent running on device.
 */
class CoverageAgentSocket(private val port: Int = 6249) {
    private var socket: Socket

    init {
        println("Making socket")
        socket = Socket("localhost", port)
        println("Socket made")
    }

    fun getCoverageMap() : ByteArray {
        socket.getOutputStream().write('d'.code)
        return socket.getInputStream().readNBytes(InstrumentationConstants.COVERAGE_MAP_SIZE)
    }

    fun resetCoverageMap() {
        socket.getOutputStream().write('r'.code)
    }
}
