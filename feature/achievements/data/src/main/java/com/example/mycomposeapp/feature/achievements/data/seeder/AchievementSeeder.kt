package com.example.mycomposeapp.feature.achievements.data.seeder

import com.example.mycomposeapp.feature.achievements.data.dto.AchievementDto

object AchievementSeeder {

    val ALL: List<AchievementDto> = listOf(

        // ── GENERAL: GAMES_PLAYED ─────────────────────────────────────────────
        AchievementDto(
            id = "gen_first_game",
            name = "First Step",
            description = "Play your first game",
            icon = "🎮",
            category = "GENERAL",
            xpReward = 50,
            conditionType = "GAMES_PLAYED",
            conditionValue = 1,
            conditionKey = null
        ),
        AchievementDto(
            id = "gen_10_games",
            name = "Getting Warmed Up",
            description = "Play 10 games",
            icon = "🔥",
            category = "GENERAL",
            xpReward = 100,
            conditionType = "GAMES_PLAYED",
            conditionValue = 10,
            conditionKey = null
        ),
        AchievementDto(
            id = "gen_50_games",
            name = "Dedicated Player",
            description = "Play 50 games",
            icon = "⭐",
            category = "GENERAL",
            xpReward = 300,
            conditionType = "GAMES_PLAYED",
            conditionValue = 50,
            conditionKey = null
        ),
        AchievementDto(
            id = "gen_100_games",
            name = "Century Club",
            description = "Play 100 games",
            icon = "💯",
            category = "GENERAL",
            xpReward = 500,
            conditionType = "GAMES_PLAYED",
            conditionValue = 100,
            conditionKey = null
        ),

        // ── GENERAL: CORRECT_ANSWERS ──────────────────────────────────────────
        AchievementDto(
            id = "gen_50_correct",
            name = "Sharp Mind",
            description = "Get 50 correct answers",
            icon = "✅",
            category = "GENERAL",
            xpReward = 150,
            conditionType = "CORRECT_ANSWERS",
            conditionValue = 50,
            conditionKey = null
        ),
        AchievementDto(
            id = "gen_200_correct",
            name = "Answer Machine",
            description = "Get 200 correct answers",
            icon = "🧠",
            category = "GENERAL",
            xpReward = 400,
            conditionType = "CORRECT_ANSWERS",
            conditionValue = 200,
            conditionKey = null
        ),

        // ── GENERAL: BEST_STREAK ──────────────────────────────────────────────
        AchievementDto(
            id = "gen_streak_5",
            name = "On a Roll",
            description = "Achieve a streak of 5",
            icon = "🔥",
            category = "GENERAL",
            xpReward = 100,
            conditionType = "BEST_STREAK",
            conditionValue = 5,
            conditionKey = null
        ),
        AchievementDto(
            id = "gen_streak_10",
            name = "Unstoppable",
            description = "Achieve a streak of 10",
            icon = "⚡",
            category = "GENERAL",
            xpReward = 250,
            conditionType = "BEST_STREAK",
            conditionValue = 10,
            conditionKey = null
        ),

        // ── GENERAL: LEVEL_REACHED ────────────────────────────────────────────
        AchievementDto(
            id = "gen_level_5",
            name = "Rising Star",
            description = "Reach level 5",
            icon = "🌟",
            category = "GENERAL",
            xpReward = 200,
            conditionType = "LEVEL_REACHED",
            conditionValue = 5,
            conditionKey = null
        ),
        AchievementDto(
            id = "gen_level_10",
            name = "Seasoned Pro",
            description = "Reach level 10",
            icon = "🏆",
            category = "GENERAL",
            xpReward = 500,
            conditionType = "LEVEL_REACHED",
            conditionValue = 10,
            conditionKey = null
        ),

        // ── MOVIES: HIGH_SCORE on MOVIES_cover ────────────────────────────────
        AchievementDto(
            id = "movies_cover_100",
            name = "Movie Buff",
            description = "Score 100 points in Movies Cover",
            icon = "🎬",
            category = "MOVIES",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "MOVIES_cover"
        ),
        AchievementDto(
            id = "movies_cover_500",
            name = "Cover Expert",
            description = "Score 500 points in Movies Cover",
            icon = "🎞️",
            category = "MOVIES",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "MOVIES_cover"
        ),
        AchievementDto(
            id = "movies_cover_1000",
            name = "Cinema Master",
            description = "Score 1000 points in Movies Cover",
            icon = "🎭",
            category = "MOVIES",
            xpReward = 600,
            conditionType = "HIGH_SCORE",
            conditionValue = 1000,
            conditionKey = "MOVIES_cover"
        ),

        // ── MOVIES: HIGH_SCORE on MOVIES_plot ─────────────────────────────────
        AchievementDto(
            id = "movies_plot_100",
            name = "Plot Seeker",
            description = "Score 100 points in Movies Plot",
            icon = "📝",
            category = "MOVIES",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "MOVIES_plot"
        ),
        AchievementDto(
            id = "movies_plot_500",
            name = "Storyteller",
            description = "Score 500 points in Movies Plot",
            icon = "📖",
            category = "MOVIES",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "MOVIES_plot"
        ),

        // ── MOVIES: HIGH_SCORE on MOVIES_emoji ────────────────────────────────
        AchievementDto(
            id = "movies_emoji_100",
            name = "Emoji Decoder",
            description = "Score 100 points in Movies Emoji",
            icon = "😀",
            category = "MOVIES",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "MOVIES_emoji"
        ),

        // ── GAMES: HIGH_SCORE on GAMES_cover ─────────────────────────────────
        AchievementDto(
            id = "games_cover_100",
            name = "Game Cover Fan",
            description = "Score 100 points in Games Cover",
            icon = "🕹️",
            category = "GAMES",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "GAMES_cover"
        ),
        AchievementDto(
            id = "games_cover_500",
            name = "Box Art Pro",
            description = "Score 500 points in Games Cover",
            icon = "🎮",
            category = "GAMES",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "GAMES_cover"
        ),

        // ── GAMES: HIGH_SCORE on GAMES_games_screenshot ───────────────────────
        AchievementDto(
            id = "games_screenshot_100",
            name = "Screenshot Scout",
            description = "Score 100 points in Games Screenshot",
            icon = "📷",
            category = "GAMES",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "GAMES_games_screenshot"
        ),
        AchievementDto(
            id = "games_screenshot_500",
            name = "Screenshot Legend",
            description = "Score 500 points in Games Screenshot",
            icon = "🖼️",
            category = "GAMES",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "GAMES_games_screenshot"
        ),

        // ── GAMES: HIGH_SCORE on GAMES_games_achievement ──────────────────────
        AchievementDto(
            id = "games_achievement_100",
            name = "Achievement Hunter",
            description = "Score 100 points in Games Achievement",
            icon = "🏅",
            category = "GAMES",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "GAMES_games_achievement"
        ),

        // ── GAMES: HIGH_SCORE on GAMES_games_description ──────────────────────
        AchievementDto(
            id = "games_description_100",
            name = "Lore Master",
            description = "Score 100 points in Games Description",
            icon = "📜",
            category = "GAMES",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "GAMES_games_description"
        ),

        // ── COMICS: HIGH_SCORE on COMICS_emoji ───────────────────────────────
        AchievementDto(
            id = "comics_emoji_100",
            name = "Manga Reader",
            description = "Score 100 points in Comics Emoji",
            icon = "📚",
            category = "COMICS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "COMICS_emoji"
        ),
        AchievementDto(
            id = "comics_emoji_500",
            name = "Otaku",
            description = "Score 500 points in Comics Emoji",
            icon = "🌸",
            category = "COMICS",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "COMICS_emoji"
        ),
        AchievementDto(
            id = "comics_emoji_1000",
            name = "Manga Master",
            description = "Score 1000 points in Comics Emoji",
            icon = "⛩️",
            category = "COMICS",
            xpReward = 600,
            conditionType = "HIGH_SCORE",
            conditionValue = 1000,
            conditionKey = "COMICS_emoji"
        ),

        // ── COMICS: HIGH_SCORE on COMICS_manga_rating ─────────────────────────
        AchievementDto(
            id = "comics_rating_100",
            name = "Critic",
            description = "Score 100 points in Comics Rating",
            icon = "⭐",
            category = "COMICS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "COMICS_manga_rating"
        ),
        AchievementDto(
            id = "comics_rating_500",
            name = "Top Critic",
            description = "Score 500 points in Comics Rating",
            icon = "🌟",
            category = "COMICS",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "COMICS_manga_rating"
        ),

        // ── COMICS: HIGH_SCORE on COMICS_rankle ───────────────────────────────
        AchievementDto(
            id = "comics_rankle_100",
            name = "Rankle Champion",
            description = "Score 100 points in Comics Rankle",
            icon = "🎯",
            category = "COMICS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "COMICS_rankle"
        ),

        // ── BOOKS: HIGH_SCORE on BOOKS_cover ─────────────────────────────────
        AchievementDto(
            id = "books_cover_100",
            name = "Bookworm",
            description = "Score 100 points in Books Cover",
            icon = "📗",
            category = "BOOKS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "BOOKS_cover"
        ),
        AchievementDto(
            id = "books_cover_500",
            name = "Bibliophile",
            description = "Score 500 points in Books Cover",
            icon = "📕",
            category = "BOOKS",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "BOOKS_cover"
        ),
        AchievementDto(
            id = "books_cover_1000",
            name = "Literary Legend",
            description = "Score 1000 points in Books Cover",
            icon = "📚",
            category = "BOOKS",
            xpReward = 600,
            conditionType = "HIGH_SCORE",
            conditionValue = 1000,
            conditionKey = "BOOKS_cover"
        ),

        // ── BOOKS: HIGH_SCORE on BOOKS_plot ──────────────────────────────────
        AchievementDto(
            id = "books_plot_100",
            name = "Plot Analyst",
            description = "Score 100 points in Books Plot",
            icon = "🔍",
            category = "BOOKS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "BOOKS_plot"
        ),

        // ── BOOKS: HIGH_SCORE on BOOKS_emoji ─────────────────────────────────
        AchievementDto(
            id = "books_emoji_100",
            name = "Emoji Storyteller",
            description = "Score 100 points in Books Emoji",
            icon = "📖",
            category = "BOOKS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "BOOKS_emoji"
        ),

        // ── SPORTS: HIGH_SCORE on SPORTS_cover ───────────────────────────────
        AchievementDto(
            id = "sports_cover_100",
            name = "Sports Fan",
            description = "Score 100 points in Sports Cover",
            icon = "⚽",
            category = "SPORTS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "SPORTS_cover"
        ),
        AchievementDto(
            id = "sports_cover_500",
            name = "Sports Enthusiast",
            description = "Score 500 points in Sports Cover",
            icon = "🏀",
            category = "SPORTS",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "SPORTS_cover"
        ),
        AchievementDto(
            id = "sports_cover_1000",
            name = "Sports Champion",
            description = "Score 1000 points in Sports Cover",
            icon = "🏆",
            category = "SPORTS",
            xpReward = 600,
            conditionType = "HIGH_SCORE",
            conditionValue = 1000,
            conditionKey = "SPORTS_cover"
        ),

        // ── SPORTS: HIGH_SCORE on SPORTS_emoji ───────────────────────────────
        AchievementDto(
            id = "sports_emoji_100",
            name = "Sporty Emoji",
            description = "Score 100 points in Sports Emoji",
            icon = "🎿",
            category = "SPORTS",
            xpReward = 100,
            conditionType = "HIGH_SCORE",
            conditionValue = 100,
            conditionKey = "SPORTS_emoji"
        ),
        AchievementDto(
            id = "sports_emoji_500",
            name = "Sports Emoji Pro",
            description = "Score 500 points in Sports Emoji",
            icon = "🥇",
            category = "SPORTS",
            xpReward = 300,
            conditionType = "HIGH_SCORE",
            conditionValue = 500,
            conditionKey = "SPORTS_emoji"
        )
    )
}
