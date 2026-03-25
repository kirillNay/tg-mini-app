pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }

    val kotlinVersion = extra["kotlin.version"] as String
    val composeVersion = extra["compose.version"] as String
    val androidGradlePluginVersion = extra["android.gradle.plugin.version"] as String

    plugins {
        id("com.android.application").version(androidGradlePluginVersion)
        kotlin("multiplatform").version(kotlinVersion)
        id("org.jetbrains.kotlin.plugin.compose").version(kotlinVersion)
        id("org.jetbrains.compose").version(composeVersion)
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "coffee-order-demo"

include(":composeApp")
includeBuild("../..")
