package com.example.mycomposeapp.feature.game.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.mycomposeapp.core.data.common.HandleResponse
import com.example.mycomposeapp.core.data.di.AuthModule
import com.example.mycomposeapp.core.data.di.IgdbRetrofit
import com.example.mycomposeapp.core.data.di.TmdbRetrofit
import com.example.mycomposeapp.feature.game.data.remote.games.service.GamesService
import com.example.mycomposeapp.feature.game.data.remote.movies.TmdbApiService
import com.example.mycomposeapp.feature.game.data.repository.games.GameSearchRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.games.GamesRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieCoverGameRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieDailyPuzzleRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieQuestionRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieSearchRepositoryImpl
import com.example.mycomposeapp.feature.game.domain.repository.CoverGameRepository
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.repository.QuestionRepository
import com.example.mycomposeapp.feature.game.domain.repository.SearchRepository
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
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
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieQuestionRepository(
        tmdbApiService: TmdbApiService
    ): QuestionRepository {
        return MovieQuestionRepositoryImpl(tmdbApiService)
    }

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieCoverGameRepository(
        tmdbApiService: TmdbApiService
    ): CoverGameRepository {
        return MovieCoverGameRepositoryImpl(tmdbApiService)
    }

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieSearchRepository(
        tmdbApiService: TmdbApiService
    ): SearchRepository {
        return MovieSearchRepositoryImpl(tmdbApiService)
    }

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieDailyPuzzleRepository(
        firestore: FirebaseFirestore,
        dataStore: DataStore<Preferences>
    ): DailyPuzzleRepository {
        return MovieDailyPuzzleRepositoryImpl(firestore, dataStore)
    }

    @Provides
    @Singleton
    fun provideGamesService(
        @IgdbRetrofit retrofit: Retrofit
    ): GamesService = retrofit.create(GamesService::class.java)

    @Provides
    @Singleton
    fun provideGamesRepository(
        remote: GamesService,
        handleResponse: HandleResponse
    ): GamesRepository {
        return GamesRepositoryImpl(
            remote = remote,
            handleResponse = handleResponse
        )
    }
    @Provides
    @IntoMap
    @StringKey("GAMES")
    fun provideGameSearchRepository(
        repo: GameSearchRepositoryImpl
    ): SearchRepository = repo
}
