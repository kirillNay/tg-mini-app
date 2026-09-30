plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

val composeVersion = property("compose.version") as String
val material3Version = property("compose.material3.version") as String

kotlin {
    android {
        namespace = "com.kirillnay.tgminiapp.samples.coffee.shared"
        compileSdk = 37
        minSdk = 24
    }

    val iosArm64 = iosArm64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    listOf(
        iosArm64,
        iosSimulatorArm64,
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoffeeOrderDemo"
            isStatic = true
        }
    }

    js {
        outputModuleName.set("coffee-order-demo")
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer()).apply {
                    open = true
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            api("org.jetbrains.compose.runtime:runtime:$composeVersion")
            api("org.jetbrains.compose.foundation:foundation:$composeVersion")
            implementation("org.jetbrains.compose.material3:material3:$material3Version")
            api("org.jetbrains.compose.ui:ui:$composeVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
        }
        jsMain.dependencies {
            implementation("io.github.kirillNay:tg-mini-app:2.0.0")
        }
    }
}

