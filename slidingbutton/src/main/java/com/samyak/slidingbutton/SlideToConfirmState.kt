package com.samyak.slidingbutton

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Anchor points for the horizontal sliding gesture.
 */
enum class SlideAnchor {
    /** Starting resting position. */
    Start,

    /** Ending confirmed position. */
    End
}

/**
 * Lifecycle status of the [SlideToConfirmButton].
 */
enum class SlideToConfirmStatus {
    /** Resting at start position, awaiting user swipe. */
    Idle,

    /** Currently being dragged by the user. */
    Sliding,

    /** User completed swipe; awaiting asynchronous operation. */
    Loading,

    /** Action completed and confirmed. */
    Completed;

    val isIdle: Boolean get() = this == Idle
    val isSliding: Boolean get() = this == Sliding
    val isLoading: Boolean get() = this == Loading
    val isCompleted: Boolean get() = this == Completed
}

/**
 * State holder for [SlideToConfirmButton].
 *
 * Encapsulates the underlying [AnchoredDraggableState] and high-level button lifecycle status.
 *
 * @param initialStatus Initial lifecycle status (defaults to [SlideToConfirmStatus.Idle]).
 */
@Stable
class SlideToConfirmState(
    initialStatus: SlideToConfirmStatus = SlideToConfirmStatus.Idle
) {
    /**
     * Underlying Foundation [AnchoredDraggableState].
     */
    val draggableState: AnchoredDraggableState<SlideAnchor> = AnchoredDraggableState(
        initialValue = if (initialStatus == SlideToConfirmStatus.Completed) SlideAnchor.End else SlideAnchor.Start
    )

    /**
     * Current lifecycle status of the button.
     */
    var status: SlideToConfirmStatus by mutableStateOf(initialStatus)
        internal set

    /**
     * True when the button is idle and awaiting interaction.
     */
    val isIdle: Boolean get() = status == SlideToConfirmStatus.Idle

    /**
     * True when the thumb is currently being dragged.
     */
    val isSliding: Boolean get() = status == SlideToConfirmStatus.Sliding

    /**
     * True when an async operation is underway.
     */
    val isLoading: Boolean get() = status == SlideToConfirmStatus.Loading

    /**
     * True when slide has completed and confirmed.
     */
    val isCompleted: Boolean get() = status == SlideToConfirmStatus.Completed

    /**
     * Current drag progress between [SlideAnchor.Start] and [SlideAnchor.End],
     * normalized in the range `0.0f .. 1.0f`.
     */
    val progress: Float
        get() {
            return if (draggableState.anchors.hasPositionFor(SlideAnchor.End)) {
                draggableState.progress(SlideAnchor.Start, SlideAnchor.End)
            } else if (status == SlideToConfirmStatus.Completed) {
                1f
            } else {
                0f
            }
        }

    /**
     * Current raw pixel offset of the draggable thumb.
     */
    val offset: Float
        get() {
            return if (draggableState.anchors.hasPositionFor(SlideAnchor.End)) {
                draggableState.requireOffset()
            } else {
                0f
            }
        }

    /**
     * Resets the button back to the starting resting state.
     *
     * @param animate Whether to smoothly animate back to [SlideAnchor.Start].
     */
    suspend fun reset(animate: Boolean = true) {
        if (draggableState.anchors.hasPositionFor(SlideAnchor.Start)) {
            if (animate) {
                draggableState.animateTo(SlideAnchor.Start)
            } else {
                draggableState.snapTo(SlideAnchor.Start)
            }
        }
        status = SlideToConfirmStatus.Idle
    }

    /**
     * Programmatically moves the button to the completed state.
     *
     * @param animate Whether to smoothly animate to [SlideAnchor.End].
     */
    suspend fun complete(animate: Boolean = true) {
        if (draggableState.anchors.hasPositionFor(SlideAnchor.End)) {
            if (animate) {
                draggableState.animateTo(SlideAnchor.End)
            } else {
                draggableState.snapTo(SlideAnchor.End)
            }
        }
        status = SlideToConfirmStatus.Completed
    }

    /**
     * Transitions the button into or out of the loading state.
     */
    fun setLoading(loading: Boolean = true) {
        status = if (loading) SlideToConfirmStatus.Loading else SlideToConfirmStatus.Idle
    }

    internal fun onEndSettled() {
        if (status != SlideToConfirmStatus.Completed && status != SlideToConfirmStatus.Loading) {
            status = SlideToConfirmStatus.Completed
        }
    }

    internal fun onDragStarted() {
        if (status == SlideToConfirmStatus.Idle) {
            status = SlideToConfirmStatus.Sliding
        }
    }

    internal fun onDragCancelled() {
        if (status == SlideToConfirmStatus.Sliding) {
            status = SlideToConfirmStatus.Idle
        }
    }

    companion object {
        /**
         * The default [Saver] implementation for [SlideToConfirmState].
         */
        val Saver: Saver<SlideToConfirmState, SlideToConfirmStatus> = Saver(
            save = { it.status },
            restore = { SlideToConfirmState(initialStatus = it) }
        )
    }
}

/**
 * Creates and remembers a [SlideToConfirmState] across recompositions and configuration changes.
 *
 * @param initialStatus Initial lifecycle status (defaults to [SlideToConfirmStatus.Idle]).
 */
@Composable
fun rememberSlideToConfirmState(
    initialStatus: SlideToConfirmStatus = SlideToConfirmStatus.Idle
): SlideToConfirmState {
    return rememberSaveable(saver = SlideToConfirmState.Saver) {
        SlideToConfirmState(initialStatus = initialStatus)
    }
}
