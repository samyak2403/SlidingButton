package com.samyak.slidingbutton

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Production-quality, accessible, expressive "SlideToConfirmButton" built with Jetpack Compose
 * and Foundation's [AnchoredDraggableState].
 *
 * Designed to provide modern tactile feedback, responsive spring physics, and accessible controls
 * for high-intent actions such as payments, order confirmations, or deletions.
 *
 * @param onSlideComplete Invoked when the user successfully slides past the completion threshold (85%).
 * @param modifier The modifier to be applied to the button layout.
 * @param state The state object controlling and observing the slide interaction.
 * @param text The prompt text displayed inside the track.
 * @param textStyle The typography style for the track label.
 * @param colors The color specifications for track, thumb, and progress states.
 * @param shape The shape of the outer track and draggable thumb.
 * @param trackHeight The overall height of the slide button.
 * @param thumbSize The diameter/size of the draggable circular thumb.
 * @param trackPadding The inner padding between the track boundary and the thumb.
 * @param thumbElevation The shadow elevation applied to the thumb.
 * @param showProgressTrack Whether to show a colored progress fill behind the thumb during sliding.
 * @param progressGradient Optional gradient brush for the progress track fill.
 * @param shimmerLabel Whether to display a subtle shimmering light sweep on the label while idle.
 * @param enabled Controls the enabled state of the button. When false, interactions are disabled.
 * @param snapAnimationSpec Spring animation specification used when the thumb snaps back or completes.
 * @param thumbIcon Composable slot for the icon inside the thumb during idle/sliding states.
 */
@Composable
fun SlideToConfirmButton(
    onSlideComplete: () -> Unit,
    modifier: Modifier = Modifier,
    state: SlideToConfirmState = rememberSlideToConfirmState(),
    text: String = "Slide to confirm",
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    colors: SlideToConfirmColors = SlideToConfirmDefaults.colors(),
    shape: Shape = CircleShape,
    trackHeight: Dp = SlideToConfirmDefaults.DefaultHeight,
    thumbSize: Dp = SlideToConfirmDefaults.DefaultThumbSize,
    trackPadding: Dp = SlideToConfirmDefaults.DefaultTrackPadding,
    thumbElevation: Dp = SlideToConfirmDefaults.DefaultElevation,
    showProgressTrack: Boolean = true,
    progressGradient: Brush? = null,
    shimmerLabel: Boolean = true,
    enabled: Boolean = true,
    snapAnimationSpec: AnimationSpec<Float> = SlideToConfirmDefaults.ExpressiveSpringSpec,
    completedIcon: (@Composable () -> Unit)? = null,
    thumbIcon: @Composable (status: SlideToConfirmStatus) -> Unit = { status ->
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = colors.thumbIconColor(enabled, status),
            modifier = Modifier.size(thumbSize * 0.52f)
        )
    }
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val paddingPx = with(density) { trackPadding.toPx() }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    var trackWidthPx by remember { mutableIntStateOf(0) }

    // Fling behavior: 85% release threshold
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = state.draggableState,
        positionalThreshold = { distance -> distance * SlideToConfirmDefaults.DefaultThreshold },
        animationSpec = snapAnimationSpec
    )

    // Completion observation & haptics
    LaunchedEffect(state) {
        snapshotFlow { state.draggableState.settledValue }
            .collect { settledValue ->
                if (settledValue == SlideAnchor.End && !state.isCompleted) {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    state.onEndSettled()
                    onSlideComplete()
                } else if (settledValue == SlideAnchor.Start && state.isSliding) {
                    state.onDragCancelled()
                }
            }
    }

    // Offset monitor to update state to Sliding
    LaunchedEffect(state) {
        snapshotFlow {
            if (state.draggableState.anchors.hasPositionFor(SlideAnchor.End)) {
                state.draggableState.requireOffset()
            } else {
                0f
            }
        }.collect { currentOffset ->
            if (currentOffset > 2f && state.isIdle) {
                state.onDragStarted()
            }
        }
    }

    // Idle shimmer transition
    val infiniteTransition = rememberInfiniteTransition(label = "shimmerTransition")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    val baseTextColor = colors.textColor(enabled)

    // Bouncy checkmark entry scale animation
    val checkmarkScale = remember { Animatable(0.2f) }
    LaunchedEffect(state.status) {
        if (state.status == SlideToConfirmStatus.Completed) {
            checkmarkScale.snapTo(0.2f)
            checkmarkScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    val stateDesc = when (state.status) {
        SlideToConfirmStatus.Idle -> "Idle"
        SlideToConfirmStatus.Sliding -> "Sliding"
        SlideToConfirmStatus.Loading -> "Loading"
        SlideToConfirmStatus.Completed -> "Confirmed"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .onSizeChanged { size ->
                if (size.width > 0 && size.width != trackWidthPx) {
                    trackWidthPx = size.width
                    val maxDragDistance = (size.width - thumbSizePx - (paddingPx * 2)).coerceAtLeast(0f)
                    if (maxDragDistance > 0f) {
                        val newAnchors = DraggableAnchors {
                            SlideAnchor.Start at 0f
                            SlideAnchor.End at maxDragDistance
                        }
                        state.draggableState.updateAnchors(newAnchors)
                        // If already completed prior to resize/rotation, remain pinned at End
                        if (state.isCompleted) {
                            coroutineScope.launch {
                                state.draggableState.snapTo(SlideAnchor.End)
                            }
                        }
                    }
                }
            }
            .clip(shape)
            .background(colors.trackColor(enabled, state.status))
            .then(
                if (colors.trackBorderColor != Color.Transparent) {
                    Modifier.border(1.dp, colors.trackBorderColor, shape)
                } else {
                    Modifier
                }
            )
            // Progress fill (lambda modifier -> zero recomposition during drag)
            .drawBehind {
                if (showProgressTrack && state.draggableState.anchors.hasPositionFor(SlideAnchor.End)) {
                    val currentOffset = state.draggableState.requireOffset()
                    val isSlidingOrDone = currentOffset > 1.5f || state.isCompleted

                    // Only draw progress track if actively sliding or completed (prevents peeking at rest)
                    if (isSlidingOrDone) {
                        val pillRadius = CornerRadius(size.height / 2f, size.height / 2f)
                        val trackBrush = progressGradient ?: SolidColor(colors.progressTrackColor)

                        val fillLength = if (state.isCompleted) {
                            size.width
                        } else {
                            (paddingPx + currentOffset + (thumbSizePx / 2f)).coerceAtMost(size.width)
                        }

                        if (isRtl) {
                            val startX = (size.width - fillLength).coerceAtLeast(0f)
                            val fillWidth = size.width - startX
                            drawRoundRect(
                                brush = trackBrush,
                                topLeft = Offset(startX, 0f),
                                size = Size(fillWidth, size.height),
                                cornerRadius = pillRadius
                            )
                        } else {
                            drawRoundRect(
                                brush = trackBrush,
                                topLeft = Offset.Zero,
                                size = Size(fillLength, size.height),
                                cornerRadius = pillRadius
                            )
                        }
                    }
                }
            }
            // Accessibility: TalkBack action "Confirm" + standard click action
            .semantics {
                role = Role.Button
                this.contentDescription = text
                stateDescription = stateDesc
                onClick(label = "Confirm") {
                    if (enabled && (state.isIdle || state.isSliding)) {
                        coroutineScope.launch {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            state.complete()
                            onSlideComplete()
                        }
                        true
                    } else {
                        false
                    }
                }
                customActions = listOf(
                    CustomAccessibilityAction("Confirm") {
                        if (enabled && (state.isIdle || state.isSliding)) {
                            coroutineScope.launch {
                                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                state.complete()
                                onSlideComplete()
                            }
                            true
                        } else {
                            false
                        }
                    }
                )
            }
    ) {
        // Track Label (fades away as thumb is dragged; zero recomposition via graphicsLayer & drawWithContent)
        Text(
            text = text,
            style = textStyle.copy(color = baseTextColor),
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    val progress = if (state.draggableState.anchors.hasPositionFor(SlideAnchor.End)) {
                        state.draggableState.progress(SlideAnchor.Start, SlideAnchor.End)
                    } else {
                        0f
                    }
                    alpha = if (state.isCompleted) 0f else (1f - progress * 1.5f).coerceIn(0f, 1f)
                }
                .drawWithContent {
                    drawContent()
                    if (shimmerLabel && enabled && state.isIdle) {
                        val width = size.width.coerceAtLeast(100f)
                        val sweepCenter = shimmerProgress * width
                        drawRect(
                            brush = Brush.horizontalGradient(
                                0.0f to Color.Transparent,
                                0.5f to Color.White.copy(alpha = 0.50f),
                                1.0f to Color.Transparent,
                                startX = sweepCenter - 140f,
                                endX = sweepCenter + 140f
                            ),
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        )

        // Draggable Thumb (offset lambda -> zero recomposition during drag)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = trackPadding)
                .offset {
                    val rawOffset = if (state.draggableState.anchors.hasPositionFor(SlideAnchor.End)) {
                        state.draggableState.requireOffset()
                    } else {
                        0f
                    }
                    val x = if (isRtl) -rawOffset.roundToInt() else rawOffset.roundToInt()
                    IntOffset(x = x, y = 0)
                }
                .anchoredDraggable(
                    state = state.draggableState,
                    orientation = Orientation.Horizontal,
                    enabled = enabled && (state.isIdle || state.isSliding),
                    reverseDirection = isRtl,
                    flingBehavior = flingBehavior
                )
                .shadow(
                    elevation = if (enabled) thumbElevation else 0.dp,
                    shape = shape,
                    clip = false
                )
                .size(thumbSize)
                .clip(shape)
                .background(colors.thumbColor(enabled, state.status)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = state.status,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.75f) togetherWith
                        fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.75f)
                },
                label = "thumbIconTransition"
            ) { targetStatus ->
                when (targetStatus) {
                    SlideToConfirmStatus.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(thumbSize * 0.52f),
                            color = colors.thumbIconColor(enabled, targetStatus),
                            strokeWidth = 2.5.dp
                        )
                    }

                    SlideToConfirmStatus.Completed -> {
                        if (completedIcon != null) {
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    scaleX = checkmarkScale.value
                                    scaleY = checkmarkScale.value
                                }
                            ) {
                                completedIcon()
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Confirmed",
                                tint = colors.thumbIconColor(enabled, targetStatus),
                                modifier = Modifier
                                    .size(thumbSize * 0.58f)
                                    .graphicsLayer {
                                        scaleX = checkmarkScale.value
                                        scaleY = checkmarkScale.value
                                    }
                            )
                        }
                    }

                    SlideToConfirmStatus.Idle, SlideToConfirmStatus.Sliding -> {
                        thumbIcon(targetStatus)
                    }
                }
            }
        }
    }
}
