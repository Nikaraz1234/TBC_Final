plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.splash"
}

dependencies {
    implementation(libs.datastore.preferences)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.turbine)
}
