plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.library.compose")
}

android {
    namespace = "com.example.mycomposeapp.core.ui"
}

dependencies {
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
