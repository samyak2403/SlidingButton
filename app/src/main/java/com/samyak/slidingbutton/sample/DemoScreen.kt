package com.samyak.slidingbutton.sample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samyak.slidingbutton.SlideToConfirmButton
import com.samyak.slidingbutton.SlideToConfirmDefaults
import com.samyak.slidingbutton.SlideToConfirmStatus
import com.samyak.slidingbutton.rememberSlideToConfirmState
import com.samyak.slidingbutton.ui.theme.SlidingButtonTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main Demo Screen showcasing Material 3 tonal shades and real-world configurations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SlideToConfirmButton",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Material 3 Expressive Tonal Shades",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Flagship: Material Sage & Moss "Slide to unlock" (exact match to user reference design)
            SampleUnlockCard()

            // Sample A: Sapphire "Slide to pay"
            SampleACard()

            // Sample B: Emerald "Async Transfer"
            SampleBCard()

            // Sample C: Crimson "Destructive Action"
            SampleCCard()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Compact, responsive status badge with color-coded dot indicator.
 */
@Composable
fun StatusBadge(status: SlideToConfirmStatus, modifier: Modifier = Modifier) {
    val (bg, fg, dotColor) = when (status) {
        SlideToConfirmStatus.Idle -> Triple(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            MaterialTheme.colorScheme.onSurfaceVariant,
            Color(0xFF79747E)
        )
        SlideToConfirmStatus.Sliding -> Triple(
            Color(0xFFFFF0C2),
            Color(0xFF7A5800),
            Color(0xFFE8B931)
        )
        SlideToConfirmStatus.Loading -> Triple(
            Color(0xFFD3E3FD),
            Color(0xFF0442A0),
            Color(0xFF1A73E8)
        )
        SlideToConfirmStatus.Completed -> Triple(
            Color(0xFFCEEAD6),
            Color(0xFF0D652D),
            Color(0xFF1E8E3E)
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

/**
 * Flagship Reference Sample: Material Moss & Sage "Slide to unlock"
 * Exactly matching the user's reference design with dark olive track,
 * sage green progress gradient, lock icon, and contour outline.
 */
@Composable
fun SampleUnlockCard(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberSlideToConfirmState()
    var isUnlocked by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF6B8756)
                    )
                    Text(
                        text = "Material Sage: Slide to unlock",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                StatusBadge(status = state.status)
            }

            Text(
                text = "Dark moss track with sage-to-olive progress gradient fill, matching the reference Material You palette.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Exact Material Shades from Reference
            val sageUnlockColors = SlideToConfirmDefaults.colors(
                trackColor = Color(0xFF283421),
                progressTrackColor = Color(0xFFB8D89C),
                thumbColor = Color(0xFFB8D89C),
                thumbIconColor = Color.White,
                textColor = Color(0xFF859C75),
                completedTrackColor = Color(0xFF38492E),
                completedThumbColor = Color(0xFFD6ECC0),
                completedThumbIconColor = Color(0xFF283421),
                trackBorderColor = Color(0xFF1B2416)
            )

            val sageProgressGradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFFB8D89C),
                    Color(0xFF50663F)
                )
            )

            SlideToConfirmButton(
                onSlideComplete = {
                    isUnlocked = true
                },
                state = state,
                text = if (isUnlocked) "UNLOCKED" else "SLIDE TO UNLOCK",
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                colors = sageUnlockColors,
                progressGradient = sageProgressGradient,
                showProgressTrack = true,
                thumbElevation = 4.dp,
                completedIcon = {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Unlocked",
                        tint = sageUnlockColors.completedThumbIconColor,
                        modifier = Modifier.size(22.dp)
                    )
                },
                thumbIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            if (isUnlocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                isUnlocked = false
                                state.reset()
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lock Again")
                    }
                }
            }
        }
    }
}

/**
 * Sample A: Clean "Slide to pay" implementation in Sapphire / Cobalt tones.
 */
@Composable
fun SampleACard(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberSlideToConfirmState()
    var paymentConfirmed by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sapphire: Slide to pay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "$49.99",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Instant swipe-to-confirm with expressive spring snap back if released before 85% threshold.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SlideToConfirmButton(
                onSlideComplete = {
                    paymentConfirmed = true
                },
                state = state,
                text = "Slide to pay $49.99",
                shimmerLabel = true,
                showProgressTrack = true,
                thumbIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )

            AnimatedVisibility(
                visible = paymentConfirmed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Payment Approved!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                paymentConfirmed = false
                                state.reset()
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset")
                    }
                }
            }
        }
    }
}

/**
 * Sample B: Progress Track, Async Loading State, and Reset in Emerald tones.
 */
@Composable
fun SampleBCard(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberSlideToConfirmState()

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emerald: Async Transfer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                StatusBadge(status = state.status)
            }

            Text(
                text = "Shows smooth progress fill behind thumb, transitions to Loading on swipe, then morphs to checkmark after simulated transfer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Modern fintech emerald palette
            val sampleBColors = SlideToConfirmDefaults.colors(
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                progressTrackColor = Color(0xFFC4EED0),
                thumbColor = Color(0xFF0F5223),
                thumbIconColor = Color.White,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                completedTrackColor = Color(0xFF1E8E3E),
                completedThumbColor = Color.White,
                completedThumbIconColor = Color(0xFF1E8E3E)
            )

            SlideToConfirmButton(
                onSlideComplete = {
                    coroutineScope.launch {
                        // Switch to loading state to show circular indicator in thumb
                        state.setLoading(true)
                        delay(1600) // Simulate network call
                        // Morph to completed checkmark
                        state.complete()
                    }
                },
                state = state,
                text = "Slide to transfer funds",
                showProgressTrack = true,
                colors = sampleBColors
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = {
                        coroutineScope.launch {
                            state.reset()
                        }
                    },
                    enabled = !state.isIdle,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset state.reset()")
                }
            }
        }
    }
}

/**
 * Sample C: Destructive action styling in Crimson tones with interactive toggle.
 */
@Composable
fun SampleCCard(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberSlideToConfirmState()
    var isEnabled by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Crimson: Destructive",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isEnabled) "Active" else "Locked",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )
                }
            }

            Text(
                text = "Custom color palette (error/destructive) with interactive disabled state demonstration.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val destructiveColors = SlideToConfirmDefaults.colors(
                trackColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                progressTrackColor = MaterialTheme.colorScheme.errorContainer,
                thumbColor = MaterialTheme.colorScheme.error,
                thumbIconColor = MaterialTheme.colorScheme.onError,
                textColor = MaterialTheme.colorScheme.onErrorContainer,
                completedTrackColor = MaterialTheme.colorScheme.error,
                completedThumbColor = MaterialTheme.colorScheme.surface,
                completedThumbIconColor = MaterialTheme.colorScheme.error
            )

            SlideToConfirmButton(
                onSlideComplete = {
                    // Confirmed destructive action
                },
                state = state,
                text = "Slide to delete account",
                colors = destructiveColors,
                enabled = isEnabled,
                thumbIcon = { status ->
                    Icon(
                        imageVector = if (!isEnabled) Icons.Default.Lock else Icons.Default.Delete,
                        contentDescription = null,
                        tint = destructiveColors.thumbIconColor(isEnabled, status),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            if (state.isCompleted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                state.reset()
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DemoScreenPreview() {
    SlidingButtonTheme {
        DemoScreen()
    }
}

@PreviewLightDark
@Composable
private fun SampleUnlockPreview() {
    SlidingButtonTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SampleUnlockCard()
        }
    }
}

@PreviewLightDark
@Composable
private fun SampleAPreview() {
    SlidingButtonTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SampleACard()
        }
    }
}

@PreviewLightDark
@Composable
private fun SampleBPreview() {
    SlidingButtonTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SampleBCard()
        }
    }
}
