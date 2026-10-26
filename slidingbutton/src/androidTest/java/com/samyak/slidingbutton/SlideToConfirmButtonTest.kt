package com.samyak.slidingbutton

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android instrumentation tests for [SlideToConfirmButton].
 *
 * Verifies Compose UI touch gestures, 85% snapping thresholds, TalkBack accessibility actions,
 * and state transitions on Android devices and emulators.
 */
@RunWith(AndroidJUnit4::class)
class SlideToConfirmButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun swipeRight_triggersOnSlideComplete() {
        var completed = false
        val testTag = "SlideToConfirm"

        composeTestRule.setContent {
            SlideToConfirmButton(
                onSlideComplete = { completed = true },
                text = "Slide to confirm",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag(testTag)
            )
        }

        // Perform full right swipe gesture across the button
        composeTestRule.onNodeWithTag(testTag).performTouchInput {
            swipeRight()
        }

        composeTestRule.waitForIdle()

        assertTrue("Expected onSlideComplete to be invoked after swipeRight()", completed)
    }

    @Test
    fun partialSwipeBelowThreshold_snapsBackAndDoesNotComplete() {
        var completed = false
        val testTag = "SlideToConfirmThreshold"

        composeTestRule.setContent {
            SlideToConfirmButton(
                onSlideComplete = { completed = true },
                text = "Slide to confirm",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag(testTag)
            )
        }

        // Drag only 35% across the width (below the 85% threshold)
        composeTestRule.onNodeWithTag(testTag).performTouchInput {
            down(centerLeft)
            moveTo(centerLeft + Offset(width * 0.35f, 0f))
            up()
        }

        composeTestRule.waitForIdle()

        assertFalse("Expected onSlideComplete NOT to be invoked on drag release below 85%", completed)
    }

    @Test
    fun disabledButton_doesNotTriggerOnSlideComplete() {
        var completed = false
        val testTag = "DisabledSlideButton"

        composeTestRule.setContent {
            SlideToConfirmButton(
                onSlideComplete = { completed = true },
                enabled = false,
                text = "Slide to confirm",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag(testTag)
            )
        }

        composeTestRule.onNodeWithTag(testTag).performTouchInput {
            swipeRight()
        }

        composeTestRule.waitForIdle()

        assertFalse("Disabled button should ignore touch swipe gestures", completed)
    }

    @Test
    fun accessibilityAction_triggersOnSlideComplete() {
        var completed = false
        val testTag = "A11ySlideButton"

        composeTestRule.setContent {
            SlideToConfirmButton(
                onSlideComplete = { completed = true },
                text = "Slide to confirm",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag(testTag)
            )
        }

        // TalkBack accessibility action: fetch semantics and trigger "Confirm"
        val node = composeTestRule.onNodeWithTag(testTag).fetchSemanticsNode()
        val actions: List<CustomAccessibilityAction>? = node.config.getOrNull(SemanticsActions.CustomActions)
        val confirmAction = actions?.firstOrNull { it.label == "Confirm" }

        assertTrue("Confirm accessibility action must be provided for TalkBack", confirmAction != null)
        confirmAction?.action?.invoke()

        composeTestRule.waitForIdle()

        assertTrue("Invoking Confirm accessibility action must trigger onSlideComplete", completed)
    }

    @Test
    fun stateReset_restoresIdleStatus() = runBlocking {
        val state = SlideToConfirmState(initialStatus = SlideToConfirmStatus.Completed)
        assertEquals(SlideToConfirmStatus.Completed, state.status)
        assertTrue(state.isCompleted)

        state.reset(animate = false)

        assertEquals(SlideToConfirmStatus.Idle, state.status)
        assertTrue(state.isIdle)
        assertFalse(state.isCompleted)
    }
}
