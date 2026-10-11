package com.arttvad9r.subtrack

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arttvad9r.subtrack.ui.UiTags
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SubscriptionUiFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetAppData() {
        val app = composeRule.activity.application as SubtrackApplication
        runBlocking {
            app.container.repository.subscriptions.first().forEach { subscription ->
                app.container.repository.delete(subscription)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.uiAutomation.grantRuntimePermission(
                app.packageName,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        }
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Подписок пока нет")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun addEditDeleteSubscription() {
        composeRule.onNodeWithText("Добавить подписку").performClick()

        composeRule.onNodeWithTag(UiTags.NAME).performTextInput("ChatGPT Plus")
        composeRule.onNodeWithTag(UiTags.PRICE).performTextInput("20")
        composeRule.onNodeWithTag(UiTags.CURRENCY).performTextReplacement("USD")

        composeRule.onNodeWithTag(UiTags.DATE).performClick()
        composeRule.onNodeWithText("Отмена").assertExists().performClick()

        composeRule.onNodeWithTag(UiTags.ADVANCED).performScrollTo().performClick()
        composeRule.onNodeWithTag(UiTags.NOTE).performScrollTo().performTextInput("Official site")
        composeRule.onNodeWithTag(UiTags.URL).performScrollTo().performTextInput("not a url")
        composeRule.onNodeWithTag(UiTags.SAVE).assertIsNotEnabled()
        composeRule.onNodeWithTag(UiTags.URL).performTextReplacement("chatgpt.com")
        composeRule.onNodeWithTag(UiTags.SAVE).assertIsEnabled().performClick()

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("ChatGPT Plus").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("20 $").assertExists()
        composeRule.onNodeWithText("Official site").assertExists()

        composeRule.onNodeWithContentDescription("Изменить").performClick()
        composeRule.onNodeWithTag(UiTags.NAME).performTextReplacement("ChatGPT Plus edited")
        composeRule.onNodeWithTag(UiTags.PRICE).performTextReplacement("25.50")
        composeRule.onNodeWithTag(UiTags.SAVE).performClick()

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("ChatGPT Plus edited").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("25.5 $").assertExists()

        composeRule.onNodeWithTag(UiTags.DELETE).performScrollTo().performClick()
        composeRule.onNodeWithText("Удалить").performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Подписок пока нет").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun detailSurvivesActivityRecreation() {
        composeRule.onNodeWithText("Добавить подписку").performClick()
        composeRule.onNodeWithTag(UiTags.NAME).performTextInput("Persistent subscription")
        composeRule.onNodeWithTag(UiTags.PRICE).performTextInput("199")
        composeRule.onNodeWithTag(UiTags.SAVE).performClick()

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Persistent subscription").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.activityRule.scenario.recreate()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Persistent subscription").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("199 ₽").assertExists()
    }
}
