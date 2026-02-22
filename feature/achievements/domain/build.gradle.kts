plugins { id("mycomposeapp.kotlin.jvm") }

dependencies {
    implementation(projects.core.domain)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
