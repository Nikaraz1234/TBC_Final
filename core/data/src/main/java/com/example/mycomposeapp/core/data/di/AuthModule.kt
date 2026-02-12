package com.example.mycomposeapp.core.data.di

import com.example.mycomposeapp.core.data.repository.AuthRepositoryImpl
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    @Singleton
    class HandleResponse @Inject constructor()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        handleResponse: com.example.mycomposeapp.core.data.common.HandleResponse
    ): AuthRepository {
        return AuthRepositoryImpl(
            firebaseAuth,
            handleResponse
        )
    }
}
