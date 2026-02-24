package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class CompleteDailyChallengeUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend operator fun invoke() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        dataStoreManager.setPreference(PreferenceKeys.DAILY_CHALLENGE_COMPLETED_DATE, today)
    }
}
