import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "io.github.kirillNay"
version = "2.0.0"

val composeVersion = property("compose.version") as String

repositories {
    google()
    mavenCentral()
}

kotlin {
    js {
        outputModuleName.set("mini-app")
        browser()
        // Compose requires an executable binary so Skiko is bundled for UI tests (CMP-4906).
        binaries.executable()
    }
    sourceSets {
        jsMain.dependencies {
            api("org.jetbrains.compose.runtime:runtime:$composeVersion")
            api("org.jetbrains.compose.foundation:foundation:$composeVersion")
            api("org.jetbrains.compose.ui:ui:$composeVersion")
        }
    }
}

// Credentials and signing keys are read from Gradle properties:
// mavenCentralUsername, mavenCentralPassword, signingInMemoryKey, signingInMemoryKeyPassword
// (in ~/.gradle/gradle.properties or as ORG_GRADLE_PROJECT_* environment variables on CI).
val hasSigningKey = providers.gradleProperty("signingInMemoryKey").isPresent

mavenPublishing {
    configure(KotlinMultiplatform(javadocJar = JavadocJar.Empty()))
    publishToMavenCentral(automaticRelease = true)
    if (hasSigningKey) {
        signAllPublications()
    }

    coordinates(group.toString(), "tg-mini-app", version.toString())

    pom {
        name.set("Telegram mini app KMP")
        description.set("Library for creating telegram mini apps with Kotlin and Compose Multiplatform.")
        url.set("https://github.com/kirillNay/tg-mini-app")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("kirillNay")
                name.set("Kirill Nayduik")
                email.set("kirill.nayduikkn1@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/kirillNay/tg-mini-app")
        }
    }
}
