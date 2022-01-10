package com.example.rally.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rally.ui.theme.RallyDialogThemeOverlay
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun RallyTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    TextField(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusEvent {
                if (it.isFocused || it.hasFocus) {
                    coroutineScope.launch {
                        delay(100)
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        enabled = enabled,
        trailingIcon = trailingIcon,
        textStyle = MaterialTheme.typography.body1
    )
}

@Composable
internal fun RallyAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissText: String = "STÄNG",
    confirmContent: @Composable () -> Unit = {
        Text(text = "RADERA", color = MaterialTheme.colors.error) },
    title: (@Composable () -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    RallyDialogThemeOverlay {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = title,
            text = content,
            buttons = {
                Column {
                    Divider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss,
                            shape = RectangleShape,
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            Text(text = dismissText)
                        }

                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = onConfirm,
                            shape = RectangleShape,
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            confirmContent()
                        }
                    }
                }
            }
        )
    }
}

@Composable
internal fun <T> RallyChipGroup(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    selectedItemString: (T) -> String,
    selectedItemColor: Color = MaterialTheme.colors.primary,
    onItemSelected: (T) -> Unit
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
    ) {
        items.distinct().forEach {
            RallyChip(
                checked = (it == selectedItem),
                onClick = { onItemSelected(it) },
                themeColor = selectedItemColor
            ) {
                Text(text = selectedItemString(it), style = MaterialTheme.typography.subtitle1)
            }
        }
    }
}

@Composable
internal fun RallyChip(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(percent = 25),
    elevation: Dp = 0.dp,
    themeColor: Color = MaterialTheme.colors.primary,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val color = if (checked) {
        themeColor
    } else {
        MaterialTheme.colors.onSurface
    }

    Surface(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .defaultMinSize(minHeight = 32.dp),
        elevation = elevation,
        shape = shape,
        color = color.copy(0.1f),
        contentColor = color.copy(0.87f)
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    enabled = enabled,
                    role = Role.RadioButton,
                    indication = rememberRipple()
                )
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}


