plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.hilt")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.mycomposeapp.feature.game.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.feature.game.domain)

    implementation(libs.bundles.networking)
    implementation(libs.datastore.preferences)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
}
