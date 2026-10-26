package com.samyak.slidingbutton

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents the colors used in different parts and lifecycle states of the [SlideToConfirmButton].
 */
@Immutable
class SlideToConfirmColors(
    val trackColor: Color,
    val progressTrackColor: Color,
    val thumbColor: Color,
    val thumbIconColor: Color,
    val textColor: Color,
    val completedTrackColor: Color,
    val completedThumbColor: Color,
    val completedThumbIconColor: Color,
    val disabledTrackColor: Color,
    val disabledThumbColor: Color,
    val disabledTextColor: Color,
    val disabledThumbIconColor: Color,
    val trackBorderColor: Color = Color.Transparent
) {
    /**
     * Resolves the track background color based on [enabled] and [status].
     */
    fun trackColor(enabled: Boolean, status: SlideToConfirmStatus): Color = when {
        !enabled -> disabledTrackColor
        status == SlideToConfirmStatus.Completed -> completedTrackColor
        else -> trackColor
    }

    /**
     * Resolves the thumb background color based on [enabled] and [status].
     */
    fun thumbColor(enabled: Boolean, status: SlideToConfirmStatus): Color = when {
        !enabled -> disabledThumbColor
        status == SlideToConfirmStatus.Completed -> completedThumbColor
        else -> thumbColor
    }

    /**
     * Resolves the thumb icon/indicator color based on [enabled] and [status].
     */
    fun thumbIconColor(enabled: Boolean, status: SlideToConfirmStatus): Color = when {
        !enabled -> disabledThumbIconColor
        status == SlideToConfirmStatus.Completed -> completedThumbIconColor
        else -> thumbIconColor
    }

    /**
     * Resolves the label text color based on [enabled].
     */
    fun textColor(enabled: Boolean): Color = if (enabled) textColor else disabledTextColor

    /**
     * Creates a copy of this [SlideToConfirmColors], allowing selective overrides.
     */
    fun copy(
        trackColor: Color = this.trackColor,
        progressTrackColor: Color = this.progressTrackColor,
        thumbColor: Color = this.thumbColor,
        thumbIconColor: Color = this.thumbIconColor,
        textColor: Color = this.textColor,
        completedTrackColor: Color = this.completedTrackColor,
        completedThumbColor: Color = this.completedThumbColor,
        completedThumbIconColor: Color = this.completedThumbIconColor,
        disabledTrackColor: Color = this.disabledTrackColor,
        disabledThumbColor: Color = this.disabledThumbColor,
        disabledTextColor: Color = this.disabledTextColor,
        disabledThumbIconColor: Color = this.disabledThumbIconColor,
        trackBorderColor: Color = this.trackBorderColor
    ): SlideToConfirmColors = SlideToConfirmColors(
        trackColor = trackColor,
        progressTrackColor = progressTrackColor,
        thumbColor = thumbColor,
        thumbIconColor = thumbIconColor,
        textColor = textColor,
        completedTrackColor = completedTrackColor,
        completedThumbColor = completedThumbColor,
        completedThumbIconColor = completedThumbIconColor,
        disabledTrackColor = disabledTrackColor,
        disabledThumbColor = disabledThumbColor,
        disabledTextColor = disabledTextColor,
        disabledThumbIconColor = disabledThumbIconColor,
        trackBorderColor = trackBorderColor
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SlideToConfirmColors) return false

        if (trackColor != other.trackColor) return false
        if (progressTrackColor != other.progressTrackColor) return false
        if (thumbColor != other.thumbColor) return false
        if (thumbIconColor != other.thumbIconColor) return false
        if (textColor != other.textColor) return false
        if (completedTrackColor != other.completedTrackColor) return false
        if (completedThumbColor != other.completedThumbColor) return false
        if (completedThumbIconColor != other.completedThumbIconColor) return false
        if (disabledTrackColor != other.disabledTrackColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledTextColor != other.disabledTextColor) return false
        if (disabledThumbIconColor != other.disabledThumbIconColor) return false
        if (trackBorderColor != other.trackBorderColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackColor.hashCode()
        result = 31 * result + progressTrackColor.hashCode()
        result = 31 * result + thumbColor.hashCode()
        result = 31 * result + thumbIconColor.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + completedTrackColor.hashCode()
        result = 31 * result + completedThumbColor.hashCode()
        result = 31 * result + completedThumbIconColor.hashCode()
        result = 31 * result + disabledTrackColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()
        result = 31 * result + disabledThumbIconColor.hashCode()
        result = 31 * result + trackBorderColor.hashCode()
        return result
    }
}

/**
 * Default values and factory methods for [SlideToConfirmButton].
 */
object SlideToConfirmDefaults {
    /**
     * Default overall height of the slide button.
     */
    val DefaultHeight: Dp = 56.dp

    /**
     * Default diameter of the draggable thumb.
     */
    val DefaultThumbSize: Dp = 48.dp

    /**
     * Default internal padding between the track boundary and the thumb.
     */
    val DefaultTrackPadding: Dp = 4.dp

    /**
     * Default soft shadow elevation for the thumb.
     */
    val DefaultElevation: Dp = 3.dp

    /**
     * Default drag threshold percentage (85% distance required to complete slide).
     */
    const val DefaultThreshold: Float = 0.85f

    /**
     * Expressive motion spring specification: damping ratio 0.75f, stiffness 380f.
     */
    val ExpressiveSpringSpec: SpringSpec<Float> = spring(
        dampingRatio = 0.75f,
        stiffness = 380f
    )

    /**
     * Resolves standard colors mapped to the active [MaterialTheme.colorScheme].
     */
    @Composable
    fun colors(
        trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        progressTrackColor: Color = MaterialTheme.colorScheme.primaryContainer,
        thumbColor: Color = MaterialTheme.colorScheme.primary,
        thumbIconColor: Color = MaterialTheme.colorScheme.onPrimary,
        textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        completedTrackColor: Color = MaterialTheme.colorScheme.primary,
        completedThumbColor: Color = MaterialTheme.colorScheme.surface,
        completedThumbIconColor: Color = MaterialTheme.colorScheme.primary,
        disabledTrackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledThumbColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledThumbIconColor: Color = MaterialTheme.colorScheme.surface,
        trackBorderColor: Color = Color.Transparent
    ): SlideToConfirmColors {
        return SlideToConfirmColors(
            trackColor = trackColor,
            progressTrackColor = progressTrackColor,
            thumbColor = thumbColor,
            thumbIconColor = thumbIconColor,
            textColor = textColor,
            completedTrackColor = completedTrackColor,
            completedThumbColor = completedThumbColor,
            completedThumbIconColor = completedThumbIconColor,
            disabledTrackColor = disabledTrackColor,
            disabledThumbColor = disabledThumbColor,
            disabledTextColor = disabledTextColor,
            disabledThumbIconColor = disabledThumbIconColor,
            trackBorderColor = trackBorderColor
        )
    }

    /**
     * Generates a smooth horizontal gradient brush for the progress fill behind the thumb.
     */
    fun progressGradient(startColor: Color, endColor: Color): Brush {
        return Brush.horizontalGradient(listOf(startColor, endColor))
    }
}
