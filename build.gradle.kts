plugins {
    kotlin("jvm") version "2.1.20" apply false
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "pt.isel.reversi"
version = "0.0.1"


allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")
}