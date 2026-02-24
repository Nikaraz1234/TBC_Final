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
    implementation(projects.testUtils)

    implementation(libs.coil.compose)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.turbine)
}
