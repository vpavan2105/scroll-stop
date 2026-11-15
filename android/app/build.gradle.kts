plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.reelsblocker"
    compileSdk = 35

    defaultConfig {
        applicationId = "app.scrollstop"
        minSdk = 26          // Android 8.0+
        targetSdk = 35
        versionCode = 9
        versionName = "5.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

// No extra libraries: only the Android framework + Kotlin standard library.
dependencies { }
