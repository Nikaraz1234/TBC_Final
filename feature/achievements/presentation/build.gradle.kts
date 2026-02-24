plugins { id("mycomposeapp.android.feature") }

android {
    namespace = "com.example.mycomposeapp.feature.achievements.presentation"
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(projects.feature.achievements.domain)
    implementation(projects.testUtils)
    implementation(libs.androidx.compose.material.icons.extended)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.turbine)
}
