plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.main.presentation"
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(projects.feature.game.domain)
    implementation(libs.androidx.compose.material.icons.extended)
}
