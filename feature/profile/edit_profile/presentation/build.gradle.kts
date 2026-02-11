plugins {
    id("mycomposeapp.android.feature")
}


android {
    namespace = "com.example.mycomposeapp.feature.profile.edit_profile.presentation"

}
dependencies {
    implementation(projects.core.presentation)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.coreUi)
    implementation(projects.feature.profile.editProfile.domain)

    implementation("androidx.compose.foundation:foundation")
    implementation(libs.datastore.preferences)
}