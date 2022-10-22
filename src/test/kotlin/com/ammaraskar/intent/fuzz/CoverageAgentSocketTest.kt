package com.ammaraskar.intent.fuzz

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import kotlin.test.assertEquals

internal class CoverageAgentSocketTest {

    private lateinit var fakeCoverageAgent: FakeCoverageAgent

    @Before
    fun setUp() {
        // Create a faked out coverage agent server on a random port.
        fakeCoverageAgent = FakeCoverageAgent()
    }

    @After
    fun tearDown() {
        fakeCoverageAgent.shutDown()
    }

    @Test
    fun testThatConnectEstablishesConnection() {
        CoverageAgentSocket(fakeCoverageAgent.port)
    }

    @Test
    fun testRetrievesCoverageMapCorrectly() {
        // Set all even indices to 1 and odds to 2.
        fakeCoverageAgent.mapCoverageInPlace { if ((it % 2) == 0) 1 else 2 }

        val coverageSocket = CoverageAgentSocket(fakeCoverageAgent.port)
        val coverageMap = coverageSocket.getCoverageMap()
        assertEquals(coverageMap[0], 1)
        assertEquals(coverageMap[1], 2)
        assertEquals(coverageMap[2], 1)
    }

    @Test
    fun testCoverageMapCanBeReset() {
        // Set the first value in the fake coverageMap to 1 to start with.
        fakeCoverageAgent.fakeCoverageMap[0] = 1

        val coverageSocket = CoverageAgentSocket(fakeCoverageAgent.port)
        val coverageMap = coverageSocket.getCoverageMap()
        assertEquals(coverageMap[0], 1)

        coverageSocket.resetCoverageMap()
        val newCoverageMap = coverageSocket.getCoverageMap()
        assertEquals(newCoverageMap[0], 0)
    }
}
