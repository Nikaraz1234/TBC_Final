plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.welcome.presentation"
}

dependencies {
    implementation(libs.datastore.preferences)
    implementation(libs.bundles.google.credentials)
    implementation(projects.testUtils)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.turbine)
}
