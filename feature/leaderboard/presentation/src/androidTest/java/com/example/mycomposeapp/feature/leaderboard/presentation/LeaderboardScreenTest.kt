package com.example.mycomposeapp.feature.leaderboard.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LeaderboardScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun renders_basic_ui() {
        val users = fakeUsers()
        val state = LeaderboardContract.State(
            isLoading = false,
            users = users,
            filteredUsers = users,
            modes = listOf("Daily", "Weekly", "All Time"),
            selectedMode = "Daily",
            categories = listOf(
                LeaderboardFilter.Movie,
                LeaderboardFilter.Books
            ),
            selectedCategory = LeaderboardFilter.Movie,
            selectedStatsKey = null
        )

        rule.setContent {
            MyComposeAppTheme {
                LeaderboardContent(state = state, onEvent = {})
            }
        }

        rule.onNodeWithTag(LeaderboardTestTags.TITLE).assertIsDisplayed()
        rule.onNodeWithTag(LeaderboardTestTags.CATEGORY_DROPDOWN).assertIsDisplayed()
        rule.onNodeWithTag(LeaderboardTestTags.MODES_ROW).assertIsDisplayed()
        rule.onNodeWithTag(LeaderboardTestTags.mode("Daily")).assertIsDisplayed()
        rule.onNodeWithTag(LeaderboardTestTags.TOP_PODIUM).assertIsDisplayed()
        rule.onNodeWithTag(LeaderboardTestTags.USER_LIST).assertIsDisplayed()
        rule.onNodeWithTag(LeaderboardTestTags.userRow("4")).assertIsDisplayed()
    }

    @Test
    fun clicking_mode_sends_event() {
        val users = fakeUsers()
        var lastEvent: LeaderboardContract.Event? = null

        val state = LeaderboardContract.State(
            isLoading = false,
            users = users,
            filteredUsers = users,
            modes = listOf("Daily", "Weekly", "All Time"),
            selectedMode = "Daily",
            categories = emptyList(),
            selectedCategory = null,
            selectedStatsKey = null
        )

        rule.setContent {
            MyComposeAppTheme {
                LeaderboardContent(state = state, onEvent = { lastEvent = it })
            }
        }

        rule.onNodeWithTag(LeaderboardTestTags.mode("Weekly")).performClick()
        assertEquals(LeaderboardContract.Event.ModeChanged("Weekly"), lastEvent)
    }

    @Test
    fun selecting_category_sends_event() {
        val users = fakeUsers()
        var lastEvent: LeaderboardContract.Event? = null

        val state = LeaderboardContract.State(
            isLoading = false,
            users = users,
            filteredUsers = users,
            modes = listOf("Daily"),
            selectedMode = "Daily",
            categories = listOf(
                LeaderboardFilter.Movie,
                LeaderboardFilter.Books
            ),
            selectedCategory = null,
            selectedStatsKey = null
        )

        rule.setContent {
            MyComposeAppTheme {
                LeaderboardContent(state = state, onEvent = { lastEvent = it })
            }
        }

        rule.onNodeWithTag(LeaderboardTestTags.CATEGORY_DROPDOWN).performClick()
        rule.onNodeWithText("Books").performClick()

        assertEquals(
            LeaderboardContract.Event.CategoryChanged(LeaderboardFilter.Books),
            lastEvent
        )
    }

    private fun fakeUsers(): List<User> = listOf(
        User(userId = "1", username = "Nika", photoUrl = ""),
        User(userId = "2", username = "Luka", photoUrl = ""),
        User(userId = "3", username = "Giorgi", photoUrl = ""),
        User(userId = "4", username = "Mariam", photoUrl = ""),
        User(userId = "5", username = "Ana", photoUrl = ""),
    )
}