package com.ammaraskar.intent.fuzz

import jadx.api.DecompilationMode
import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import java.io.File

class ApkAnalyzer(private val apkFile: String) {
    init {
        val jadxArgs = JadxArgs()
        jadxArgs.decompilationMode = DecompilationMode.FALLBACK;
        jadxArgs.isDeobfuscationOn = false;
        jadxArgs.setInputFile(File(apkFile))

        JadxDecompiler(jadxArgs).use {
            it.load();

            val manifestResource =
                it.resources.stream().filter { resource -> resource.originalName.equals("AndroidManifest.xml") }
                    .findFirst();
            if (!manifestResource.isPresent) {
                throw IllegalArgumentException("APK does not contain AndroidManifest.xml")
            }

            val contents = manifestResource.get().loadContent();
            println("originalName: ${manifestResource.get().originalName}")
            println("deobfName: ${manifestResource.get().deobfName}")
            println(contents.text.codeStr)

            val targets = parseIntentTargetsFromManifest(contents.text.codeStr)
        }
    }
}
