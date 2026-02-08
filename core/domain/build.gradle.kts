plugins {
    id("mycomposeapp.kotlin.jvm")
}

dependencies {
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.datetime)

    testImplementation(kotlin("test"))
}
