plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.game.archive"
}

dependencies {
    implementation(projects.feature.game.domain)
}
