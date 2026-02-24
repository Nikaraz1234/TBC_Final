package com.example.mycomposeapp.core.data.di

import com.example.mycomposeapp.core.data.common.HandleResponse
import com.example.mycomposeapp.core.data.repository.UserRepositoryImpl
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        handleResponse: HandleResponse
    ): UserRepository {
        return UserRepositoryImpl(firebaseAuth, firestore, handleResponse )
    }
}
