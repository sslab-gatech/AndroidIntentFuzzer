package com.ammaraskar.intent.fuzz

import java.lang.IllegalArgumentException
import javax.xml.parsers.DocumentBuilderFactory

class IntentTarget(val className: String, val actionNames: Collection<String>) {
}

fun parseIntentTargetsFromManifest(manifestXML: String) : List<IntentTarget> {
    val targets = mutableListOf<IntentTarget>()

    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val document = documentBuilder.parse(manifestXML.byteInputStream())

    // Find all <intent-filter> tags and iterate over them.
    val intentFilters = document.getElementsByTagName("intent-filter")
    for (i in 0 until intentFilters.length) {
        val node = intentFilters.item(i)

        val containingComponent = node.parentNode

        // Get the class name of the component containing this intent-filter.
        val intentClass = containingComponent.attributes.getNamedItem("android:name")?.nodeValue
            ?: throw IllegalArgumentException("Manifest has an intent-filter component without an android:name")

        // Check to see if the component is exported. That is, either the "android:exported" attribute is marked as
        // "true" or if it isn't present, it takes a default value of true when there is an <intent-filter>
        val exportedAttribute =
            containingComponent.attributes.getNamedItem("android:exported")?.nodeValue
                ?: "true"
        val isExported = exportedAttribute == "true"

        // Only add to list of targets if it is exported.
        if (!isExported) {
            continue
        }

        val actionNames = mutableListOf<String>()
        // Gather all the action tags.
        for (j in 0 until node.childNodes.length) {
            val intentFilterChild = node.childNodes.item(j)

            // Check if this is a <category> or an <action>
            if (intentFilterChild.nodeName == "action") {
                actionNames.add(intentFilterChild.attributes.getNamedItem("android:name").nodeValue)
            }
        }

        // Make sure we have at least one action otherwise this isn't a valid intent target.
        if (actionNames.isEmpty()) {
            throw IllegalArgumentException("Manifest has an intent-filter without any <action> tags")
        }

        targets.add(IntentTarget(intentClass, actionNames))
    }

    return targets
}
