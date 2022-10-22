package com.ammaraskar.intent.fuzz

import org.junit.Ignore
import org.junit.Test

internal class ApkAnalyzerTest {
    @Ignore("Ignored until we actually commit actual testing apk files into the repo")
    @Test
    fun testLoadsApk() {
        ApkAnalyzer("C:\\Users\\ammar\\workspace\\gatech\\android-intent-fuzz\\VulnerableApp\\app\\build\\outputs\\apk\\debug\\app-debug.apk")
    }
}