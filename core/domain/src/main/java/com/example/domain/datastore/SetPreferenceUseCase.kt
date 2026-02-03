package com.example.domain.datastore

import androidx.datastore.preferences.core.Preferences
import com.example.domain.repository.DataStoreManager

class SetPreferenceUseCase(private val preferencesRepository: DataStoreManager) {
    suspend operator fun <T> invoke(key: Preferences.Key<T>, value: T) =
        preferencesRepository.setPreference(key, value)
}