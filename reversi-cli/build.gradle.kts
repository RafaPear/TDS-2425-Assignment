plugins {
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

dependencies {
    // kotlin-stdlib
    implementation(kotlin("stdlib"))
    implementation("com.github.RafaPear:KtFlag:1.4.1")
    implementation(project(":reversi-core"))
}