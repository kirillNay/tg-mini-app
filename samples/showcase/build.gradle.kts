plugins {
    // Load plugins once in the root classloader so sibling modules share their build services.
    id("com.android.application") apply false
    id("com.android.kotlin.multiplatform.library") apply false
    kotlin("multiplatform") apply false
    id("org.jetbrains.kotlin.plugin.compose") apply false
    id("org.jetbrains.compose") apply false
}

allprojects {
    group = "com.kirillnay.tgminiapp.samples"
    version = "1.0.0"
}
