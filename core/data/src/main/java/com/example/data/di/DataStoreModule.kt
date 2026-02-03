package com.example.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.data.common.datastore.DataStoreManagerImpl
import com.example.domain.repository.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.datastore.preferences.core.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("app_preferences") }
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        dataStore: DataStore<Preferences>
    ): DataStoreManager {
        return DataStoreManagerImpl(dataStore)
    }
}