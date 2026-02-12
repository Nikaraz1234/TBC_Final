package com.example.mycomposeapp.feature.profile.edit_profile.data.di

import android.content.Context
import androidx.work.WorkManager
import com.example.mycomposeapp.core.data.common.HandleResponse
import com.example.mycomposeapp.feature.profile.edit_profile.data.repository.ProfileRepositoryImpl
import com.example.mycomposeapp.feature.profile.edit_profile.domain.repository.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EditProfileDataModule {

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        firestore: FirebaseFirestore,
        workManager: WorkManager,
        handleResponse: HandleResponse
    ): ProfileRepository {
        return ProfileRepositoryImpl(firestore, workManager, handleResponse)
    }
}
