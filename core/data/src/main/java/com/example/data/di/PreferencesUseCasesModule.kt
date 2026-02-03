package com.example.data.di

import com.example.domain.datastore.GetPreferenceUseCase
import com.example.domain.datastore.RemovePreferenceUseCase
import com.example.domain.datastore.SetPreferenceUseCase
import com.example.domain.repository.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PreferencesUseCasesModule {

    @Provides
    fun provideGetPreferenceUseCase(
        repo: DataStoreManager
    ): GetPreferenceUseCase = GetPreferenceUseCase(repo)

    @Provides
    fun provideSetPreferenceUseCase(
        repo: DataStoreManager
    ): SetPreferenceUseCase = SetPreferenceUseCase(repo)

    @Provides
    fun provideRemovePreferenceUseCase(
        repo: DataStoreManager
    ): RemovePreferenceUseCase = RemovePreferenceUseCase(repo)
}
