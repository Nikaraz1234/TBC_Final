package com.example.mycomposeapp.feature.game.data.di

import com.example.mycomposeapp.core.data.di.IgdbRetrofit
import com.example.mycomposeapp.core.data.di.MalRetrofit
import com.example.mycomposeapp.core.data.di.SteamRetrofit
import com.example.mycomposeapp.core.data.di.SteamStoreRetrofit
import com.example.mycomposeapp.core.data.di.TmdbRetrofit
import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.feature.game.data.remote.comics.MalApiService
import com.example.mycomposeapp.feature.game.data.remote.games.service.GamesService
import com.example.mycomposeapp.feature.game.data.remote.games.service.SteamService
import com.example.mycomposeapp.feature.game.data.remote.games.service.SteamStoreService
import com.example.mycomposeapp.feature.game.data.remote.movies.TmdbApiService
import com.example.mycomposeapp.feature.game.data.repository.comics.MangaEmojiRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.comics.MangaSearchRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.comics.MangaRatingRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.comics.RankleRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.games.GameSearchRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.games.GamesRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieCoverGameRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieDailyPuzzleRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MoviePlotGameRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieQuestionRepositoryImpl
import com.example.mycomposeapp.feature.game.data.repository.movies.MovieSearchRepositoryImpl
import com.example.mycomposeapp.feature.game.domain.repository.CoverGameRepository
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.repository.MangaEmojiRepository
import com.example.mycomposeapp.feature.game.domain.repository.MangaRatingRepository
import com.example.mycomposeapp.feature.game.domain.repository.PlotGameRepository
import com.example.mycomposeapp.feature.game.domain.repository.QuestionRepository
import com.example.mycomposeapp.feature.game.domain.repository.RankleRepository
import com.example.mycomposeapp.feature.game.domain.repository.SearchRepository
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
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
    @Singleton
    fun provideGamesService(@IgdbRetrofit retrofit: Retrofit): GamesService {
        return retrofit.create(GamesService::class.java)
    }

    @Provides
    @Singleton
    fun provideSteamService(@SteamRetrofit retrofit: Retrofit): SteamService {
        return retrofit.create(SteamService::class.java)
    }

    @Provides
    @Singleton
    fun provideSteamStoreService(@SteamStoreRetrofit retrofit: Retrofit): SteamStoreService {
        return retrofit.create(SteamStoreService::class.java)
    }

    @Provides
    @Singleton
    fun provideMalApiService(@MalRetrofit retrofit: Retrofit): MalApiService {
        return retrofit.create(MalApiService::class.java)
    }

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieQuestionRepository(
        impl: MovieQuestionRepositoryImpl
    ): QuestionRepository = impl

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieCoverGameRepository(
        impl: MovieCoverGameRepositoryImpl
    ): CoverGameRepository = impl

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMoviePlotGameRepository(
        impl: MoviePlotGameRepositoryImpl
    ): PlotGameRepository = impl

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieSearchRepository(
        impl: MovieSearchRepositoryImpl
    ): SearchRepository = impl

    @Provides
    @IntoMap
    @StringKey("MOVIES")
    fun provideMovieDailyPuzzleRepository(
        impl: MovieDailyPuzzleRepositoryImpl
    ): DailyPuzzleRepository = impl

    @Provides
    @Singleton
    fun provideGamesRepository(
        impl: GamesRepositoryImpl
    ): GamesRepository = impl

    @Provides
    @IntoMap
    @StringKey("GAMES")
    fun provideGameSearchRepository(
        impl: GameSearchRepositoryImpl
    ): SearchRepository = impl

    @Provides
    @Singleton
    fun provideMangaRatingRepository(
        impl: MangaRatingRepositoryImpl
    ): MangaRatingRepository = impl

    @Provides
    @Singleton
    fun provideMangaRatingRepository(
        malApiService: MalApiService
    ): MangaRatingRepository = MangaRatingRepositoryImpl(malApiService)

    @Provides
    @IntoMap
    @StringKey("COMICS")
    fun provideComicsDailyPuzzleRepository(
        repo: MangaEmojiRepositoryImpl
    ): DailyPuzzleRepository = repo

    @Provides
    @IntoMap
    @StringKey("COMICS")
    fun provideMangaSearchRepository(
        repo: MangaSearchRepositoryImpl
    ): SearchRepository = repo
}
    fun provideRankleRepository(
        impl: RankleRepositoryImpl
    ): RankleRepository = impl
}
