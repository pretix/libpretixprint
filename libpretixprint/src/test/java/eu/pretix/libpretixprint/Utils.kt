package eu.pretix.libpretixprint

import java.net.URL

fun getResource(filename: String): URL? {
    return ManualTest::class.java.classLoader.getResource(filename)
}
