package com.ammaraskar.intent.fuzz

import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class IntentTargetTest {

    @Test
    fun parseIntentTargetsWorksOnARealManifest() {
        val testManifest = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android" android:versionCode="1" android:versionName="1.0" android:compileSdkVersion="30" android:compileSdkVersionCodename="11" package="com.ammaraskar.vulnerableapp" platformBuildVersionCode="30" platformBuildVersionName="11">
                <uses-sdk android:minSdkVersion="23" android:targetSdkVersion="30"/>
                <application android:theme="@style/AppTheme" android:label="@string/app_name" android:icon="@mipmap/ic_launcher" android:debuggable="true" android:allowBackup="true" android:supportsRtl="true" android:extractNativeLibs="false" android:roundIcon="@mipmap/ic_launcher_round" android:appComponentFactory="androidx.core.app.CoreComponentFactory">
                    <activity android:name="com.ammaraskar.vulnerableapp.MainActivity">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN"/>
                            <category android:name="android.intent.category.LAUNCHER"/>
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent()

        val targets = parseIntentTargetsFromManifest(testManifest)
        assertEquals(targets.size, 1)
        assertEquals(targets[0].className, "com.ammaraskar.vulnerableapp.MainActivity")
    }

    @Test
    fun ignoresTargetsThatAreNotExported() {
        val testManifest = """
            <activity android:exported="false" android:name="com.ammaraskar.vulnerableapp.MainActivity">
                <intent-filter>
                    <action android:name="android.intent.action.VIEW"/>
                </intent-filter>
            </activity>
        """.trimIndent()

        val targets = parseIntentTargetsFromManifest(testManifest)
        assertEquals(targets.size, 0)
    }

    @Test
    fun doesNotIgnoreExplicitlyExportedIntentTargets() {
        val testManifest = """
            <activity android:exported="true" android:name="com.ammaraskar.vulnerableapp.MainActivity">
                <intent-filter>
                    <action android:name="android.intent.action.VIEW"/>
                </intent-filter>
            </activity>
        """.trimIndent()

        val targets = parseIntentTargetsFromManifest(testManifest)
        assertEquals(targets.size, 1)
    }

    @Test
    fun throwsIfIntentDoesNotHaveAnyActions() {
        val testManifest = """
            <activity android:exported="true" android:name="com.ammaraskar.vulnerableapp.MainActivity">
                <intent-filter>
                </intent-filter>
            </activity>
        """.trimIndent()

        assertFailsWith<IllegalArgumentException>() {
            parseIntentTargetsFromManifest(testManifest)
        }
    }
}