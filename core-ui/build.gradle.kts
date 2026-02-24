plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.library.compose")
}

android {
    namespace = "com.example.mycomposeapp.core.ui"
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    api(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test.android)
}
