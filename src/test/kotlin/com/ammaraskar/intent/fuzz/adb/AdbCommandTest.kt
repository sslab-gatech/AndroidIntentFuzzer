package com.ammaraskar.intent.fuzz.adb

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

internal class AdbCommandTest {
    @Test
    fun testGeneratesBasicShellCommandCorrectly() {
        val command = AdbCommand.Shell("ls")
        assertEquals(command.buildCommand(), listOf("adb", "shell", "ls"))
    }

    @Test
    fun testGeneratesPushCommandCorrectly() {
        val command = AdbCommand.Push(File("file-to-push"), "/tmp/path/to/push")
        assertEquals(command.buildCommand(), listOf("adb", "push", "file-to-push", "/tmp/path/to/push"))
    }

    @Test
    fun testChangingBaseAdbCommandWorks() {
        val command = AdbCommand.Shell("ls")
        command.adbExecutable = "/path/to/adb"

        assertEquals(command.buildCommand(), listOf("/path/to/adb", "shell", "ls"))
    }
}