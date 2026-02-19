plugins {
    id("mycomposeapp.android.feature")
}

android {
    namespace = "com.example.mycomposeapp.feature.notification.presentation"
}
dependencies{
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
}