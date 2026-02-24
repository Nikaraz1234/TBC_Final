plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.hilt")
}

android {
    namespace = "com.example.mycomposeapp.feature.achievements.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.feature.achievements.domain)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
}
