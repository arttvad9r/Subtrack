package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object Radius {
    val card = RoundedCornerShape(16.dp)
    val field = RoundedCornerShape(12.dp)
    val fab = RoundedCornerShape(16.dp)
}

internal object Space {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val screen = 20.dp
}

@Composable
internal fun subtrackSwitchColors() = SwitchDefaults.colors(
    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    uncheckedBorderColor = Color.Transparent,
    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
    uncheckedIconColor = Color.Transparent,
    checkedTrackColor = MaterialTheme.colorScheme.primary,
    checkedBorderColor = Color.Transparent,
    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
    checkedIconColor = MaterialTheme.colorScheme.onPrimary,
)

@Composable
internal fun selectionChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
)
