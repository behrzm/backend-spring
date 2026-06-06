import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

// Умный поиск local.properties
val localProperties = Properties()
val searchFiles = listOf(
    rootProject.file("local.properties"),
    rootProject.file("../local.properties"),
    project.file("../local.properties"),
    project.file("../../local.properties")
)

val existingFile = searchFiles.firstOrNull { it.exists() }
if (existingFile != null) {
    existingFile.inputStream().use { localProperties.load(it) }
    println(">>> AI Debug: Loading keys from ${existingFile.absolutePath}")
} else {
    println(">>> AI Debug: local.properties NOT FOUND!")
}

val groqKey = localProperties.getProperty("GROQ_API_KEY") ?: ""
// Для физического телефона: IPv4 ПК в той же Wi-Fi (ipconfig), например http://192.168.0.5:8080/api/v1/
val apiBaseUrl = localProperties.getProperty("API_BASE_URL")?.trim().orEmpty()

android {
    namespace = "com.prolearn.codecraftfront"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.prolearn.codecraftfront"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GROQ_API_KEY", "\"$groqKey\"")
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("nl.dionsegijn:konfetti-compose:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.coil.compose)
    implementation("io.coil-kt:coil-gif:2.7.0")
}
