plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.hilt")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.mycomposeapp.feature.profile.edit_profile.data"
}
    dependencies {
        implementation(projects.feature.profile.editProfile.domain)
        implementation(projects.core.data)
        implementation(projects.core.domain)

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-storage-ktx")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

        implementation("androidx.work:work-runtime-ktx:2.9.0")


    }

