plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.eflc.mintdrop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.eflc.mintdrop"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
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
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val viewModelVersion = "2.6.2"
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.6.0"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$viewModelVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$viewModelVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$viewModelVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$viewModelVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$viewModelVersion")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.google.dagger:hilt-android:2.48.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    ksp("com.google.dagger:hilt-android-compiler:2.48.1")
    ksp("androidx.hilt:hilt-compiler:1.0.0")
    ksp("androidx.room:room-compiler:$roomVersion")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}