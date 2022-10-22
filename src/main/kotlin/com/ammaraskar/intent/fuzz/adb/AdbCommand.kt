package com.ammaraskar.intent.fuzz.adb

import java.io.File
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

sealed class AdbCommand {
    data class Push(val localFile: File, val remoteLocation: String) : AdbCommand()
    data class Shell(val shellArgs: List<String>) : AdbCommand() {
        constructor(vararg args: String) : this(args.asList())
    }

    /** Used to change the base adb command, in case it's not in the PATH. */
    var adbExecutable : String = "adb"

    fun buildCommand(): List<String> {
        val adbCommand = mutableListOf(adbExecutable)
        when (this) {
            is Push -> {
                adbCommand.add("push")
                adbCommand.add(this.localFile.path)
                adbCommand.add(this.remoteLocation)
            }
            is Shell -> {
                adbCommand.add("shell")
                adbCommand.addAll(this.shellArgs)
            }
        }
        return adbCommand
    }

    fun runCommandAndGetOutput() : String {
        val command = buildCommand()
        val proc = ProcessBuilder(command)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(60, TimeUnit.SECONDS)
        val output = proc.inputStream.bufferedReader().readText()
        if (proc.exitValue() != 0) {
            print(output)
            print(proc.errorStream.bufferedReader().readText())
            throw RuntimeException("${command.joinToString(" ")} returned exit code ${proc.exitValue()}")
        }
        return output
    }

}
