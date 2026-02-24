plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.profile.presentation"
}

dependencies {
    implementation(projects.core.presentation)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.coreUi)
    implementation(projects.testUtils)

    implementation("androidx.compose.foundation:foundation")
    implementation(libs.datastore.preferences)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.turbine)
}