package com.example.mycomposeapp.feature.game.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.mycomposeapp.core.data.di.TmdbRetrofit
import com.example.mycomposeapp.feature.game.data.remote.TmdbApiService
import com.example.mycomposeapp.feature.game.data.repository.CoverGameRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.DailyPuzzleRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.MovieQuestionRepositoryImpl
import com.example.mycomposeapp.feature.game.domain.repository.CoverGameRepository
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.repository.QuestionRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GameDataModule {

    @Provides
    @Singleton
    fun provideTmdbApiService(@TmdbRetrofit retrofit: Retrofit): TmdbApiService {
        return retrofit.create(TmdbApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        tmdbApiService: TmdbApiService
    ): QuestionRepository {
        return MovieQuestionRepositoryImpl(tmdbApiService)
    }

    @Provides
    @Singleton
    fun provideCoverGameRepository(
        tmdbApiService: TmdbApiService
    ): CoverGameRepository {
        return CoverGameRepositoryImpl(tmdbApiService)
    }

    @Provides
    @Singleton
    fun provideDailyPuzzleRepository(
        firestore: FirebaseFirestore,
        dataStore: DataStore<Preferences>
    ): DailyPuzzleRepository {
        return DailyPuzzleRepositoryImpl(firestore, dataStore)
    }
}
