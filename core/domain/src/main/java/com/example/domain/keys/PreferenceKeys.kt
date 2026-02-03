package com.example.domain.keys

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey(PreferenceConstants.TOKEN)
    val USERNAME = stringPreferencesKey(PreferenceConstants.USERNAME)
    val USER_TOKEN = stringPreferencesKey(PreferenceConstants.USER_TOKEN)
    val DARK_MODE = booleanPreferencesKey(PreferenceConstants.DARK_MODE)
}