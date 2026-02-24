package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.model.GameModeInfo
import javax.inject.Inject

class GetAvailableGameModesUseCase @Inject constructor() {
    
    operator fun invoke(): List<GameModeInfo> {
        return listOf(
            // Movies category
            GameModeInfo(
                categoryName = "Movies",
                categoryType = CategoryType.MOVIES.name,
                gameModeName = "By Cover",
                gameModeId = GameModeIds.COVER
            ),
            GameModeInfo(
                categoryName = "Movies",
                categoryType = CategoryType.MOVIES.name,
                gameModeName = "By Emoji",
                gameModeId = GameModeIds.EMOJI
            ),
            GameModeInfo(
                categoryName = "Movies",
                categoryType = CategoryType.MOVIES.name,
                gameModeName = "By Plot Summary",
                gameModeId = GameModeIds.PLOT
            ),
            
            // Games category
            GameModeInfo(
                categoryName = "Games",
                categoryType = CategoryType.GAMES.name,
                gameModeName = "By Screenshot",
                gameModeId = GameModeIds.GAME_SCREENSHOT
            ),
            GameModeInfo(
                categoryName = "Games",
                categoryType = CategoryType.GAMES.name,
                gameModeName = "By Achievements",
                gameModeId = GameModeIds.GAME_ACHIEVEMENT
            ),
            GameModeInfo(
                categoryName = "Games",
                categoryType = CategoryType.GAMES.name,
                gameModeName = "By Description",
                gameModeId = GameModeIds.GAME_DESCRIPTION
            ),
            
            // Comics category
            GameModeInfo(
                categoryName = "Comics",
                categoryType = CategoryType.COMICS.name,
                gameModeName = "Compare the Rating",
                gameModeId = GameModeIds.MANGA_RATING
            ),
            GameModeInfo(
                categoryName = "Comics",
                categoryType = CategoryType.COMICS.name,
                gameModeName = "By Emoji",
                gameModeId = GameModeIds.EMOJI
            ),
            GameModeInfo(
                categoryName = "Comics",
                categoryType = CategoryType.COMICS.name,
                gameModeName = "Guess the rating",
                gameModeId = GameModeIds.RANKLE
            )
        )
    }
}
