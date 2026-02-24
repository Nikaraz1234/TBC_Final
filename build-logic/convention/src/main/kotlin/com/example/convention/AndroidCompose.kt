package com.example.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

    dependencies {
        val bom = libs.findLibraryOrThrow("androidx-compose-bom")
        add("implementation", platform(bom))
        add("implementation", libs.findLibraryOrThrow("androidx-ui"))
        add("implementation", libs.findLibraryOrThrow("androidx-ui-graphics"))
        add("implementation", libs.findLibraryOrThrow("androidx-ui-tooling-preview"))
        add("implementation", libs.findLibraryOrThrow("androidx-material3"))
        add("implementation", libs.findLibraryOrThrow("androidx-compose-foundation"))
        add("debugImplementation", libs.findLibraryOrThrow("androidx-ui-tooling"))
    }
}
