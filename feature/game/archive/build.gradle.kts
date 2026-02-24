plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.game.archive"
}

dependencies {
    implementation(projects.feature.game.domain)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

}
