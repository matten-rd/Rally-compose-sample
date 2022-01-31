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
import androidx.compose.runtime.*
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
import com.example.rally.ui.savings.GeneralUiState
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

/**
 * Component to handle snackbar error (or general) messages to the user
 *
 * @param T [GeneralUiState]
 * @param uiState [T] This stores the message
 * @param scaffoldState To be able to show the snackbar
 * @param retryMessageText Button text
 * @param onRefresh Called if action is performed (button clicked)
 * @param onErrorDismiss Notify viewModel that the snackbar with id has been shown
 */
@Composable
fun <T : GeneralUiState> SnackbarMessageHandler(
    uiState: T,
    scaffoldState: ScaffoldState,
    retryMessageText: String? = null,
    onRefresh: () -> Unit,
    onErrorDismiss: (Long) -> Unit
) {
    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.userMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage = remember(uiState) { uiState.userMessages[0] }

        // Get the text to show on the message from resources
        //val errorMessageText: String = stringResource(errorMessage.id)
        //val retryMessageText = stringResource(id = R.string.retry)
        val errorMessageText = errorMessage.message

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefresh)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or scaffoldState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText,
                duration = SnackbarDuration.Long
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}



