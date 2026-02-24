import java.util.Properties

plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.hilt")
    alias(libs.plugins.kotlinx.serialization)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.example.mycomposeapp.core.data"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"${libs.versions.base.url.get()}\"")
        buildConfigField("String", "TMDB_BASE_URL", "\"${libs.versions.tmdb.base.url.get()}\"")
        buildConfigField("String", "TMDB_API_KEY", "\"${localProperties.getProperty("TMDB_API_KEY", "")}\"")
        buildConfigField("String", "TMDB_IMAGE_BASE_URL", "\"${libs.versions.tmdb.image.base.url.get()}\"")
        buildConfigField("String", "REQRES_API_KEY", "\"${localProperties.getProperty("REQRES_API_KEY", "")}\"")
        buildConfigField("String", "IGDB_CLIENT_ID", "\"${localProperties.getProperty("IGDB_CLIENT_ID", "")}\"")
        buildConfigField("String", "IGDB_TOKEN", "\"${localProperties.getProperty("IGDB_TOKEN", "")}\"")
        buildConfigField("String", "STEAM_API_KEY", "\"${localProperties.getProperty("STEAM_API_KEY", "")}\"")
        buildConfigField("String", "MAL_CLIENT_ID", "\"${localProperties.getProperty("MAL_CLIENT_ID")}\"")
        buildConfigField("String", "GOOGLE_BOOKS_API_KEY", "\"${localProperties.getProperty("GOOGLE_BOOKS_API_KEY", "")}\"")
    }
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.networking)
    implementation(libs.datastore.preferences)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.bundles.google.credentials)

    implementation(libs.kotlinx.datetime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test.android)

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    ksp("androidx.room:room-compiler:2.6.1")

}
