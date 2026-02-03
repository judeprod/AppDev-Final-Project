plugins {
    id("com.android.application") version "9.0.0" apply false
    id("com.android.library") version "9.0.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.7" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

