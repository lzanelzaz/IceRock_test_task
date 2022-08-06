// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
    kotlin("android") version "1.7.10" apply false
    kotlin("plugin.serialization") version "1.7.10" apply false
    kotlin("kapt") version "1.7.10" apply false
    id("com.google.dagger.hilt.android") version "2.43.1" apply false
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}