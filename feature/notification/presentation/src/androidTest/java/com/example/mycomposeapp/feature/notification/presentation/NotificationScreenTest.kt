package com.example.mycomposeapp.feature.notification.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.notification.presentation.model.NotificationUi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NotificationScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_empty_older_text_when_no_older_notifications() {
        val state = NotificationContract.State(
            isLoading = false,
            newNotifications = emptyList(),
            olderNotifications = emptyList()
        )

        rule.setContent {
            MyComposeAppTheme {
                NotificationContent(
                    state = state,
                    onEvent = {}
                )
            }
        }

        rule.onNodeWithTag(NotificationTestTags.SCREEN).assertIsDisplayed()
        rule.onNodeWithTag(NotificationTestTags.OLDER_EMPTY_TEXT).assertIsDisplayed()
    }

    @Test
    fun clicking_notification_opens_sheet() {
        val n1 = NotificationUi(
            id = "n1",
            title = "Title 1",
            body = "Body 1",
            isRead = false,
            deeplink = "",
            type = "",
            date = "10:00 01/01/2026"
        )

        val state = NotificationContract.State(
            isLoading = false,
            newNotifications = listOf(n1),
            olderNotifications = emptyList()
        )

        rule.setContent {
            MyComposeAppTheme {
                NotificationContent(
                    state = state,
                    onEvent = {}
                )
            }
        }

        rule.onNodeWithTag(NotificationTestTags.item("n1")).performClick()
        rule.onNodeWithTag(NotificationTestTags.SHEET).assertIsDisplayed()
    }

    @Test
    fun dismissing_sheet_marks_unread_as_read() {
        val n1 = NotificationUi(
            id = "n1",
            title = "Title 1",
            body = "Body 1",
            isRead = false,
            deeplink = "",
            type = "",
            date = "10:00 01/01/2026"
        )

        val state = NotificationContract.State(
            isLoading = false,
            newNotifications = listOf(n1),
            olderNotifications = emptyList()
        )

        val events = mutableListOf<NotificationContract.Event>()

        rule.setContent {
            MyComposeAppTheme {
                NotificationContent(
                    state = state,
                    onEvent = { events.add(it) }
                )
            }
        }

        rule.onNodeWithTag(NotificationTestTags.item("n1")).performClick()
        rule.onNodeWithTag(NotificationTestTags.SHEET).assertIsDisplayed()

        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()

        val markEvents = events.filterIsInstance<NotificationContract.Event.MarkAsRead>()
        assertEquals(1, markEvents.size)
        assertEquals("n1", markEvents.first().id)
    }
}