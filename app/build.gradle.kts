plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.newstripepayment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.newstripepayment"
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //  java server side
    implementation ("com.stripe:stripe-java:29.0.0")

    // Stripe Android SDK
    implementation("com.stripe:stripe-android:21.8.0")
    // Include the financial connections SDK to support US bank account as a payment method
    implementation("com.stripe:financial-connections:21.8.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.androidx.navigation.runtime.android)

//    //    Navigation
//    implementation ("androidx.navigation:navigation-compose:2.7.7")
//    implementation ("com.stripe:stripe-android:20.37.2")
//
//
//    // okhttps
//    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
//    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")


    // ViewModel and Compose integration
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // If you don't have these already
    implementation ("androidx.activity:activity-compose:1.8.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")


    implementation ("androidx.activity:activity-compose:1.8.0")
    implementation ("androidx.compose.material3:material3:1.1.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}