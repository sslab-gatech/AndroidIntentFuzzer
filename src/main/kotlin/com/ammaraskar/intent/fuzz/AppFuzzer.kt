package com.ammaraskar.intent.fuzz

import com.ammaraskar.intent.fuzz.adb.AdbCommand
import java.io.File

class AppFuzzer(private val packageName: String, private val targets: Collection<IntentTarget>) {

    private val ADB_COMMAND = "C:\\Users\\ammar\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe"

    /** Place the coverage agent in the app's startup agents directory and mark the app debuggable. */
    fun installCoverageAgent() {
        // Get the architecture of the target, the agent library we install will have to match the target.
        val architecture = getArchitecture()
        val library = File("CoverageAgent/runtime_cpp/build/intermediates/merged_native_libs/debug/out/lib/${architecture}/libcoverage_instrumenting_agent.so")
        println("adb device architecture is $architecture")
        println("Uploading instrumentation library $library")

        val pushLibraryCommand = AdbCommand.Push(library, "/data/local/tmp/")
        pushLibraryCommand.adbExecutable = ADB_COMMAND
        pushLibraryCommand.runCommandAndGetOutput()

        println("Uploaded to ${pushLibraryCommand.remoteLocation}")

        println("Making startup_agents directory")
        val makeDirectory = AdbCommand.Shell("run-as", packageName, "mkdir -p code_cache/startup_agents/")
        makeDirectory.adbExecutable = ADB_COMMAND
        makeDirectory.runCommandAndGetOutput()

        println("Copying to startup_agents")
        val copyToStartupAgents = AdbCommand.Shell("run-as", packageName, "cp /data/local/tmp/libcoverage_instrumenting_agent.so code_cache/startup_agents/")
        copyToStartupAgents.adbExecutable = ADB_COMMAND
        copyToStartupAgents.runCommandAndGetOutput()
    }

    private fun getArchitecture(): String {
        val abiCommand = AdbCommand.Shell("getprop", "ro.product.cpu.abi")
        abiCommand.adbExecutable = ADB_COMMAND
        return abiCommand.runCommandAndGetOutput().trim()
    }
}