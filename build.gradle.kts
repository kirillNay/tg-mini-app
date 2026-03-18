plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")

    id("convention.publication")
}

group = "io.github.kirillNay"
version = "1.1.0"

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

kotlin {
    js(IR) {
        outputModuleName.set("mini-app")
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
            }
        }
    }
}
