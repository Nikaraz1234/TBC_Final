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
    implementation(projects.feature.welcome.presentation)
    implementation(projects.feature.login.presentation)
    implementation(projects.feature.register.presentation)
    implementation(projects.feature.main.presentation)
    implementation(projects.feature.profile.editProfile.presentation)
    implementation(projects.feature.profile.profilePage.presentation)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.bundles.networking)
    implementation(libs.coil.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.splashscreen)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
