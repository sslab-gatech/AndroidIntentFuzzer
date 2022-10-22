package com.ammaraskar.intent.fuzz

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * This is a fake for the coverage agent running on the emulator used for testing.
 */
class FakeCoverageAgent {

    private var fakeAgentServerSocket: ServerSocket = ServerSocket(0)
    var port: Int = 0

    private var fakeAgentThread: Thread
    private var socket: Socket? = null

    /** The coverageMap sent to the client when they request a dump */
    var fakeCoverageMap: ByteArray = ByteArray(InstrumentationConstants.COVERAGE_MAP_SIZE)

    init {
        fakeAgentServerSocket.reuseAddress = true
        port = fakeAgentServerSocket.localPort
        fakeAgentThread = Thread {
            val connection = fakeAgentServerSocket.accept()
            handleConnection(connection)
        }
        fakeAgentThread.name = "fake-agent-thread"
        fakeAgentThread.start()
    }

    private fun handleConnection(socket: Socket) {
        this.socket = socket
        while (true) {
            val byte = try {
                socket.getInputStream().read()
            } catch (e: IOException) {
                println("Socket read exception: ${e.message}")
                break
            }
            // No byte available, break.
            if (byte == -1) { break }

            val command = byte.toChar()
            if (command == 'r') {
                // Reset coverage map
                for (i in fakeCoverageMap.indices) {
                    fakeCoverageMap[i] = 0
                }
            } else if (command == 'd') {
                // Dump coverage map
                socket.getOutputStream().write(fakeCoverageMap)
            }
        }
    }

    fun mapCoverageInPlace(mapFunction: (index: Int) -> Byte) {
        for (i in fakeCoverageMap.indices) {
            fakeCoverageMap[i] = mapFunction(i)
        }
    }

    fun shutDown() {
        // Close the server socket.
        fakeAgentServerSocket.close()
        // Close the connection socket if present.
        socket?.close()

        // Kill the thread.
        fakeAgentThread.interrupt()
        fakeAgentThread.join()
    }
}
