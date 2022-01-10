package com.example.rally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

@Composable
internal fun ColorPicker(
    items: List<Color>,
    selectedColor: Color,
    onColorSelected: (color: Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisAlignment = FlowMainAxisAlignment.Center
        ) {
            items.distinct().forEach { color ->
                ColorItem(
                    selected = (color == selectedColor),
                    color = color,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

/**
 * Filled circle used in color picker.
 * Selected style based on the selected color attributes.
 */
@Composable
private fun ColorItem(
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .size(56.dp)
            .clickable(onClick = onClick)
    ) {
        val colorModifier =
            if (color.luminance() < 0.1 || color.luminance() > 0.9) {
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = CircleShape
                    )
            } else {
                Modifier
                    .fillMaxSize()
                    .background(color)
            }

        Box(modifier = colorModifier) {
            if(selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = if (color.luminance() < 0.5) Color.White else Color.Black,
                    modifier = Modifier.align(Alignment.Center),
                    contentDescription = null
                )
            }
        }
    }
}
