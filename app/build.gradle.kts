plugins {
//    alias(libs.plugins.android.application)
    id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.vidyasetu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vidyasetu"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "x86", "x86_64")
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

        buildFeatures {
            viewBinding = true
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.pytorch.android.lite.v1130)
    implementation(libs.gradle)
//    implementation(libs.itext7.core)
    implementation ("com.itextpdf:itext7-core:7.1.15")   // For PDF extraction
    implementation ("com.itextpdf:kernel:7.1.15")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")  // For DOCX extraction
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(libs.okhttp)
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.firebase:firebase-messaging:23.1.0")
//    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
//    implementation("com.google.firebase:firebase-auth")
//    implementation("com.google.firebase:firebase-database")
//    implementation("com.google.firebase:firebase-storage")
//    implementation("com.google.firebase:firebase-messaging")
//    implementation("com.google.firebase:firebase-firestore")
//    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.15.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.json:json:20210307")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5") // Added navigation
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.google.code.gson:gson:2.8.9")

}

