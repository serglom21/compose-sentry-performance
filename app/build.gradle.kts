import io.sentry.android.gradle.extensions.InstrumentationFeature
import io.sentry.android.gradle.instrumentation.logcat.LogcatLevel

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.io.sentry.android.gradle)
    alias(libs.plugins.io.sentry.kotlin.compiler.gradle)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = "com.example.sentry"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sentry"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.androidx.compose.compiler.get().version
    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
}

sentry {
    debug.set(true)
    includeProguardMapping.set(false)
    autoUploadProguardMapping.set(false)
    autoUploadNativeSymbols.set(false)
    tracingInstrumentation {
        enabled.set(true)
//        features.set(setOf(
//            InstrumentationFeature.DATABASE,
//            InstrumentationFeature.FILE_IO,
//            InstrumentationFeature.OKHTTP,
//            InstrumentationFeature.COMPOSE
//        ))
        logcat {
            enabled.set(true)
            minLevel.set(LogcatLevel.WARNING)
        }

        // The set of glob patterns to exclude from instrumentation. Classes matching any of these
        // patterns in the project's sources and dependencies JARs won't be instrumented by the Sentry
        // Gradle plugin.
        //
        // Don't include the file extension. Filtering is done on compiled classes and
        // the .class suffix isn't included in the pattern matching.
        //
        // Example usage:
        // ```
        // excludes.set(setOf("com/example/donotinstrument/**", "**/*Test"))
        // ```
        //
        // Only supported when using Android Gradle plugin (AGP) version 7.4.0 and above.
        excludes.set(emptySet())
    }
    autoInstallation {
        enabled.set(true)
        sentryVersion.set(libs.io.sentry.sentry.android.get().version)
    }
}

dependencies {
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.activity.activity.compose)
    implementation(platform(libs.androidx.compose.compose.bom))
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.androidx.fragment.ktx)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
    implementation(libs.androidx.navigation.navigation.ui.ktx)
    implementation(libs.androidx.navigation.navigation.fragment.ktx)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
    implementation(libs.com.squareup.okhttp3.okhttp)
    implementation(libs.com.squareup.okhttp3.logging.interceptor)
    implementation(libs.androidx.room.room.runtime)
    implementation(libs.androidx.room.room.ktx)
    ksp(libs.androidx.room.room.compiler)
    implementation(libs.com.jakewharton.timber.timber)
}
