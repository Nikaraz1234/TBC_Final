plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.splash.presentation"
}

dependencies {
    implementation(libs.datastore.preferences)
}
