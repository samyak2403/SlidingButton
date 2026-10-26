package com.samyak.slidingbutton

import androidx.compose.foundation.gestures.DraggableAnchors
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM unit tests for [SlideToConfirmState] state transitions and progress calculations.
 */
class SlideToConfirmStateTest {

    @Test
    fun initialState_defaultsToIdle() {
        val state = SlideToConfirmState()
        assertEquals(SlideToConfirmStatus.Idle, state.status)
        assertTrue(state.isIdle)
        assertFalse(state.isSliding)
        assertFalse(state.isLoading)
        assertFalse(state.isCompleted)
        assertEquals(0f, state.progress, 0.001f)
    }

    @Test
    fun initialCompletedState_setsCompletedStatus() {
        val state = SlideToConfirmState(initialStatus = SlideToConfirmStatus.Completed)
        assertEquals(SlideToConfirmStatus.Completed, state.status)
        assertTrue(state.isCompleted)
        assertEquals(1f, state.progress, 0.001f)
    }

    @Test
    fun setLoading_updatesStatusCorrectly() {
        val state = SlideToConfirmState()
        state.setLoading(true)
        assertEquals(SlideToConfirmStatus.Loading, state.status)
        assertTrue(state.isLoading)

        state.setLoading(false)
        assertEquals(SlideToConfirmStatus.Idle, state.status)
        assertTrue(state.isIdle)
    }

    @Test
    fun dragCallbacks_transitionBetweenIdleAndSliding() {
        val state = SlideToConfirmState()
        state.onDragStarted()
        assertEquals(SlideToConfirmStatus.Sliding, state.status)
        assertTrue(state.isSliding)

        state.onDragCancelled()
        assertEquals(SlideToConfirmStatus.Idle, state.status)
        assertTrue(state.isIdle)
    }

    @Test
    fun onEndSettled_transitionsToCompleted() {
        val state = SlideToConfirmState()
        state.onDragStarted()
        state.onEndSettled()
        assertEquals(SlideToConfirmStatus.Completed, state.status)
        assertTrue(state.isCompleted)
    }

    @Test
    fun reset_restoresIdleStatus() = runBlocking {
        val state = SlideToConfirmState(initialStatus = SlideToConfirmStatus.Completed)
        state.reset(animate = false)
        assertEquals(SlideToConfirmStatus.Idle, state.status)
        assertTrue(state.isIdle)
        assertFalse(state.isCompleted)
    }

    @Test
    fun complete_setsCompletedStatus() = runBlocking {
        val state = SlideToConfirmState(initialStatus = SlideToConfirmStatus.Idle)
        state.complete(animate = false)
        assertEquals(SlideToConfirmStatus.Completed, state.status)
        assertTrue(state.isCompleted)
    }

    @Test
    fun anchorsProgress_calculatesCorrectly() {
        val state = SlideToConfirmState()
        val anchors = DraggableAnchors {
            SlideAnchor.Start at 0f
            SlideAnchor.End at 200f
        }
        state.draggableState.updateAnchors(anchors)

        assertEquals(0f, state.progress, 0.001f)
        assertEquals(0f, state.offset, 0.001f)
    }
}
