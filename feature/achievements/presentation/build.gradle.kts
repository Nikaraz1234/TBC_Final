plugins { id("mycomposeapp.android.feature") }

android {
    namespace = "com.example.mycomposeapp.feature.achievements.presentation"
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(projects.feature.achievements.domain)
    implementation(libs.androidx.compose.material.icons.extended)
}
