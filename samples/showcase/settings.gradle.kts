pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    val kotlinVersion = extra["kotlin.version"] as String
    val composeVersion = extra["compose.version"] as String
    val androidGradlePluginVersion = extra["android.gradle.plugin.version"] as String

    plugins {
        id("com.android.application").version(androidGradlePluginVersion)
        id("com.android.kotlin.multiplatform.library").version(androidGradlePluginVersion)
        kotlin("multiplatform").version(kotlinVersion)
        id("org.jetbrains.kotlin.plugin.compose").version(kotlinVersion)
        id("org.jetbrains.compose").version(composeVersion)
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "showcase"

include(":composeApp")
include(":androidApp")
includeBuild("../..")
