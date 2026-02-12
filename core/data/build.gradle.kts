plugins {
    id("mycomposeapp.android.library")
    id("mycomposeapp.android.hilt")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.mycomposeapp.core.data"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        val igdbClientId = providers.gradleProperty("IGDB_CLIENT_ID").orNull
            ?: error("Missing IGDB_CLIENT_ID")

        val igdbToken = providers.gradleProperty("IGDB_TOKEN").orNull
            ?: error("Missing IGDB_TOKEN")

        buildConfigField("String", "BASE_URL", "\"${libs.versions.base.url.get()}\"")
        buildConfigField("String", "IGDB_CLIENT_ID", "\"$igdbClientId\"")
        buildConfigField("String", "IGDB_TOKEN", "\"$igdbToken\"")
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test.android)
}
