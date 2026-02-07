package com.example.mycomposeapp.core.domain.usecase.datastore

import androidx.datastore.preferences.core.Preferences
import com.example.mycomposeapp.core.domain.repository.DataStoreManager

class RemovePreferenceUseCase(private val preferencesRepository: DataStoreManager) {
    suspend operator fun invoke(keys: List<Preferences.Key<*>>) =
        preferencesRepository.removePreferences(keys)
}