package com.example.mycomposeapp.core.domain.keys

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey("token")
    val USERNAME = stringPreferencesKey("username")
    val USER_TOKEN = stringPreferencesKey("user_token")
    val DARK_MODE = booleanPreferencesKey("dark_mode")

    val DAILY_CHALLENGE_COMPLETED_DATE = stringPreferencesKey("daily_challenge_completed_date")
    val DAILY_GOALS_DATE = stringPreferencesKey("daily_goals_date")
    val DAILY_GOALS_GAMES_PLAYED = intPreferencesKey("daily_goals_games_played")
    val DAILY_GOALS_PERFECT_SCORES = intPreferencesKey("daily_goals_perfect_scores")
    val DAILY_GOALS_CATEGORIES_TRIED = stringPreferencesKey("daily_goals_categories_tried")
}
