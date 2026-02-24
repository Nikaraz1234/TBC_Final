package com.example.mycomposeapp.feature.main.presentation.model

data class Tip(
    val id: Int,
    val emoji: String,
    val text: String
)

object Tips {
    val all = listOf(
        Tip(1, "\uD83C\uDFAC", "Pay attention to small details in movie posters — colors and fonts are big clues!"),
        Tip(2, "\uD83E\uDDE0", "Use process of elimination — rule out what you know it's NOT."),
        Tip(3, "⭐", "Daily challenges give bonus coins — don't miss them!"),
        Tip(4, "\uD83D\uDD25", "Build a streak by playing every day to earn extra rewards."),
        Tip(5, "\uD83C\uDFAF", "Focus on genres you know well first, then branch out."),
        Tip(6, "\uD83D\uDCA1", "Emoji clues often represent key plot points — think abstractly!"),
        Tip(7, "\uD83D\uDCDA", "Reading plot summaries carefully can reveal the answer in the first line."),
        Tip(8, "\uD83C\uDFC6", "Complete all daily goals to maximize your coin earnings.")
    )
}
