package com.example.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.findVersionString(name: String): String =
    findVersion(name).get().toString()

fun VersionCatalog.findLibraryOrThrow(name: String) =
    findLibrary(name).orElseThrow { NoSuchElementException("Library $name not found in version catalog") }

fun VersionCatalog.findPluginId(name: String): String =
    findPlugin(name).get().get().pluginId
