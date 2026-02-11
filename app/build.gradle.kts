plugins {
    id("mycomposeapp.android.application")
    id("mycomposeapp.android.application.compose")
    id("mycomposeapp.android.hilt")
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.mycomposeapp"

    defaultConfig {
        applicationId = "com.example.mycomposeapp"
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "BASE_URL", "\"${libs.versions.base.url.get()}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.presentation)
    implementation(projects.core.data)
    implementation(projects.coreUi)
    implementation(projects.feature.splash.presentation)
    implementation(projects.feature.welcome)
    implementation(projects.feature.login)
    implementation(projects.feature.register)
    implementation(projects.feature.profile.profilePage.presentation)
    implementation(projects.feature.profile.editProfile.presentation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.coil.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
