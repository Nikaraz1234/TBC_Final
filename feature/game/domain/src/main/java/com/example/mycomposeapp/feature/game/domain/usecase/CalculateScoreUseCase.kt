package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import javax.inject.Inject

class CalculateScoreUseCase @Inject constructor() {

    operator fun invoke(answers: List<AnswerResult>, timeLimitSeconds: Int): GameResult {
        var totalScore = 0
        var currentStreak = 0
        var bestStreak = 0
        var correctCount = 0
        var totalTime = 0

        for (answer in answers) {
            totalTime += answer.timeSpentSeconds
            if (answer.isCorrect) {
                correctCount++
                currentStreak++
                if (currentStreak > bestStreak) bestStreak = currentStreak

                val basePoints = GameConstants.BASE_POINTS
                val streakBonus = GameConstants.STREAK_BONUS_MULTIPLIER * currentStreak
                val timeBonus = calculateTimeBonus(timeLimitSeconds, answer.timeSpentSeconds)

                totalScore += basePoints + streakBonus + timeBonus
            } else {
                currentStreak = 0
            }
        }

        return GameResult(
            totalQuestions = answers.size,
            correctAnswers = correctCount,
            totalScore = totalScore,
            timeTakenSeconds = totalTime,
            bestStreak = bestStreak,
            answers = answers
        )
    }

    private fun calculateTimeBonus(timeLimitSeconds: Int, timeSpentSeconds: Int): Int {
        if (timeLimitSeconds <= 0) return 0
        val remainingTime = (timeLimitSeconds - timeSpentSeconds).coerceAtLeast(0)
        return (remainingTime * GameConstants.TIME_BONUS_MAX) / timeLimitSeconds
    }
}
