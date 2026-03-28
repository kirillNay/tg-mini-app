import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import java.util.*

plugins {
    `maven-publish`
    signing
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.secretKey"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null

// Grabbing signing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.secretKey"] = System.getenv("SIGNING_KEY")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
}

fun getExtraString(name: String) = ext[name]?.toString()
fun normalizeSecretKey(secretKey: String?): String? =
    secretKey
        ?.replace("\r\n", "\n")
        ?.replace("\\n", "\n")
        ?.trim()

ext["signing.secretKey"] = normalizeSecretKey(getExtraString("signing.secretKey"))

val hasInMemorySigningConfiguration =
    !getExtraString("signing.secretKey").isNullOrBlank() &&
        !getExtraString("signing.password").isNullOrBlank()
val hasFileSigningConfiguration =
    !getExtraString("signing.keyId").isNullOrBlank() &&
        !getExtraString("signing.password").isNullOrBlank() &&
        !getExtraString("signing.secretKeyRingFile").isNullOrBlank()
val hasSigningConfiguration = hasInMemorySigningConfiguration || hasFileSigningConfiguration

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        val publicationJavadocJar =
            tasks.register("${this@withType.name}JavadocJar", Jar::class) {
            archiveBaseName.set("${project.name}-${this@withType.name}")
            archiveClassifier.set("javadoc")
        }

        // Use a dedicated javadoc artifact per publication to avoid signing task output collisions.
        artifact(publicationJavadocJar)

        // Provide artifacts information requited by Maven Central
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
}

// Signing artifacts. Signing.* extra properties values will be used
signing {
    isRequired = hasSigningConfiguration
    if (hasInMemorySigningConfiguration) {
        useInMemoryPgpKeys(
            getExtraString("signing.keyId"),
            getExtraString("signing.secretKey"),
            getExtraString("signing.password"),
        )
        sign(publishing.publications)
    } else if (hasFileSigningConfiguration) {
        sign(publishing.publications)
    }
}
