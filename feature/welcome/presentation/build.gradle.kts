plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.welcome.presentation"
}

dependencies {
    implementation(libs.datastore.preferences)
    implementation(libs.bundles.google.credentials)
}
