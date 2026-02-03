package com.example.data.common.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManagerImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    DataStoreManager {
    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data.map { preferences -> preferences[key] ?: defaultValue }
    }

    override suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences -> preferences[key] = value }
    }

    override suspend fun removePreferences(keys: List<Preferences.Key<*>>) {
        dataStore.edit { preferences ->
            keys.forEach { key ->
                preferences.remove(key)
            }
        }
    }
}