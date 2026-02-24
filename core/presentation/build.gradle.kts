plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.library.compose")
}

android {
    namespace = "com.example.mycomposeapp.core.presentation"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.javax.inject)
    implementation(libs.datastore.preferences)
    implementation(libs.bundles.google.credentials)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
