package com.example.mycomposeapp.core.domain.usecase.datastore

import androidx.datastore.preferences.core.Preferences
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.Flow

class GetPreferenceUseCase(private val preferencesRepository: DataStoreManager) {
    operator fun <T> invoke(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        preferencesRepository.getPreference(key, defaultValue)
}