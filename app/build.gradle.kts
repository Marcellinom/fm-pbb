plugins {
    alias(libs.plugins.androidApplication)
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "app.marsel.cam_translate"
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    api("com.otaliastudios:cameraview:2.0.0-beta05")
    implementation("com.google.firebase:firebase-ml-natural-language-translate-model:20.0.7")
    implementation("com.google.firebase:firebase-ml-natural-language:22.0.0")
    implementation("com.google.firebase:firebase-ml-natural-language-language-id-model:20.0.7")
    implementation("com.google.firebase:firebase-ml-natural-language-translate:22.0.0")  // For translating text
    implementation("com.google.firebase:firebase-ml-vision:24.0.0")  // For detecting text from Image
    implementation("com.google.firebase:firebase-core:17.2.0")
    implementation("androidx.multidex:multidex:2.0.1")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}