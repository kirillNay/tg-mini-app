import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"

    id("convention.publication")
}

group = "io.github.kirillNay"
version = "1.2.0-alpha01"

repositories {
    mavenCentral()
}

val publicationSecrets = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.reader().use(::load)
    }
}

fun publicationCredential(
    propertyName: String,
    envName: String,
    legacyPropertyName: String? = null,
    legacyEnvName: String? = null,
): String? =
    publicationSecrets.getProperty(propertyName)
        ?: legacyPropertyName?.let(publicationSecrets::getProperty)
        ?: System.getenv(envName)
        ?: legacyEnvName?.let(System::getenv)

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(
                publicationCredential(
                    propertyName = "sonatypeUsername",
                    envName = "SONATYPE_USERNAME",
                    legacyPropertyName = "ossrhUsername",
                    legacyEnvName = "OSSRH_USERNAME",
                ),
            )
            password.set(
                publicationCredential(
                    propertyName = "sonatypePassword",
                    envName = "SONATYPE_PASSWORD",
                    legacyPropertyName = "ossrhPassword",
                    legacyEnvName = "OSSRH_PASSWORD",
                ),
            )
        }
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
