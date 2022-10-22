package com.ammaraskar.intent.fuzz

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

class IntentFuzzer : CliktCommand() {
    val targetApk by argument()

    override fun run() {
        echo("Hello world!")

        AppFuzzer("com.ammaraskar.vulnerableapp", listOf()).installCoverageAgent()
    }
}

fun main(args: Array<String>) = IntentFuzzer().main(args)
