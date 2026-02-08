package com.example.mycomposeapp.core.domain.keys

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey(PreferenceConstants.TOKEN)
    val USERNAME = stringPreferencesKey(PreferenceConstants.USERNAME)
    val USER_TOKEN = stringPreferencesKey(PreferenceConstants.USER_TOKEN)
    val DARK_MODE = booleanPreferencesKey(PreferenceConstants.DARK_MODE)

    val DAILY_CHALLENGE_COMPLETED_DATE = stringPreferencesKey(PreferenceConstants.DAILY_CHALLENGE_COMPLETED_DATE)
    val DAILY_GOALS_DATE = stringPreferencesKey(PreferenceConstants.DAILY_GOALS_DATE)
    val DAILY_GOALS_GAMES_PLAYED = intPreferencesKey(PreferenceConstants.DAILY_GOALS_GAMES_PLAYED)
    val DAILY_GOALS_PERFECT_SCORES = intPreferencesKey(PreferenceConstants.DAILY_GOALS_PERFECT_SCORES)
    val DAILY_GOALS_CATEGORIES_TRIED = stringPreferencesKey(PreferenceConstants.DAILY_GOALS_CATEGORIES_TRIED)
}