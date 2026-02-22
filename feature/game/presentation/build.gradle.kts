plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.game.presentation"
}

dependencies {
    implementation(projects.feature.game.domain)
    implementation(projects.feature.game.data)
    implementation(projects.feature.achievements.domain)

    implementation(libs.coil.compose)
}
