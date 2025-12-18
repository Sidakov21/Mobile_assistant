plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

android {
    namespace = "com.example.mobileassistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobileassistant"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Конфигурация для 16 KB устройств
        ndk {
            abiFilters.addAll(arrayOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    // Настройки для совместимости с 16 KB
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }

}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    // Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Themes
    implementation("com.google.android.material:material:1.11.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel with navigation compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

}