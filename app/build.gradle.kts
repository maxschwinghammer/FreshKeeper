import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.navigation.safeargs)
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.freshkeeper"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.freshkeeper"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 8
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        @Suppress("UnstableApiUsage")
        manifestPlaceholders["appAuthRedirectScheme"] = "com.freshkeeper"

        val keystoreFile = project.rootProject.file("app/keys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        val rawApiKey = properties.getProperty("API_KEY") ?: ""
        val apiKey = rawApiKey.trim('"')
        buildConfigField("String", "API_KEY", "\"$apiKey\"")

        val rawPassword = properties.getProperty("EMAIL_PASSWORD") ?: ""
        val emailPassword = rawPassword.trim('"')
        buildConfigField("String", "EMAIL_PASSWORD", "\"$emailPassword\"")
    }

    @Suppress("UnstableApiUsage")
    buildFeatures.apply {
        buildConfig = true
        compose = true
    }

    buildTypes {
        getByName("release") {
            @Suppress("UnstableApiUsage")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    @Suppress("UnstableApiUsage")
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.5"
    }
}

kapt {
    arguments {
        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
        arg("dagger.hilt.internal.useAggregatingRootProcessor", "true")
        arg("dagger.fastInit", "ENABLED")
    }
}

dependencies {
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.commonmark.commonmark)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)
    implementation(libs.firebase.perf)
    implementation(libs.tensorflow.lite)
    implementation(libs.generativeai)
    implementation(libs.android.image.cropper)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.playServicesAuth)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.espresso.core)
    implementation(libs.appauth)
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    implementation(libs.barcode.scanning)
    implementation(libs.barcode.scanning.common)
    implementation(libs.billing.ktx)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.com.journeyapps.zxing.android.embedded)
    implementation(libs.com.google.android.play.review2)
    implementation(libs.com.google.android.play.review.ktx2)
    implementation(libs.coil.compose)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.gms.google.services)
    implementation(libs.gms.play.services.mlkit.image.labeling)
    implementation(libs.google.accompanist.pager)
    implementation(libs.google.accompanist.pager.indicators)
    implementation(libs.google.core)
    implementation(libs.gson)
    implementation(libs.hilt)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.firebase.messaging)
    implementation(libs.javax.mail)
    implementation(libs.json)
    implementation(libs.junit)
    implementation(libs.material)
    implementation(libs.mlkit.image.labeling)
    implementation(libs.okhttp)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.base)
    implementation(libs.play.services.identity)
    implementation(libs.play.services.mlkit.text.recognition)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.text.recognition)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.googleid)
    implementation(libs.image.labeling.common)
    implementation(libs.litert.support.api)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)
    kapt(libs.hilt.compiler)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
