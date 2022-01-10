package com.example.rally.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rally.ui.components.*
import com.example.rally.ui.components.linechart.LineChartData
import com.example.rally.ui.components.linechart.RallyLineChart
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.flow.collect

@Composable
fun SavingsDetailScreen(
    scaffoldState: ScaffoldState,
    viewModel: SavingsDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val account = uiState.savingsItem

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SavingsDetailViewModel.DetailEvent.AccountDeleted -> onNavigateBack()
                SavingsDetailViewModel.DetailEvent.AccountUpdated -> onNavigateBack()
                SavingsDetailViewModel.DetailEvent.Error -> {
                    // TODO: Display error snackbar or alertDialog
                    println("Error")
                }
            }
        }
    }

    SnackbarMessageHandler(
        uiState = uiState,
        scaffoldState = scaffoldState,
        onRefresh = { /*TODO*/ },
        onErrorDismiss = {}
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        LoadingErrorContent(
            empty = uiState.isLoading,
            emptyContent = { FullScreenLoading(Modifier.fillMaxSize()) },
            error = uiState.isError,
            errorContent = { FullScreenError() {  } },
            loading = uiState.isLoading,
            onRefresh = { viewModel.fetchAccount() }
        ) {
            if (account != null) {
                if (uiState.isDialogOpen) {
                    RallyAlertDialog(
                        onDismiss = { viewModel.toggleDialog(false) },
                        onConfirm = { viewModel.deleteAccount() }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Är du säker på att du vill radera kontot?",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                SavingsScreenDetailContent(
                    colors = viewModel.colors,
                    timeIntervals = viewModel.allTimeIntervals,
                    accountName = account.accountName,
                    bank = account.bank,
                    currentBalance = account.currentBalance,
                    color = account.color,
                    history = account.history,
                    change = account.change,
                    isColorDialogOpen = viewModel.isColorDialogOpen,
                    selectedTimeInterval = uiState.selectedTimeInterval,
                    onAccountNameChange = { viewModel.onAccountNameChange(it) },
                    onBankNameChange = { viewModel.onBankNameChange(it) },
                    onCurrentBalanceChange = { viewModel.onCurrentBalanceChange(it) },
                    onColorChange = { viewModel.onColorChange(it) },
                    toggleColorDialog = { viewModel.toggleColorDialog(it) },
                    onTimeIntervalChange = { viewModel.onTimeIntervalChange(it) },
                    onSaveClick = viewModel::onSaveClick,
                    onDelete = { viewModel.toggleDialog(true) }
                )
            }

        }
        Spacer(modifier = Modifier.height(12.dp))

    }
}


@Composable
private fun SavingsScreenDetailContent(
    colors: List<Color>,
    timeIntervals: List<TimeInterval>,
    accountName: String,
    bank: String,
    currentBalance: String,
    color: Color,
    history: List<LineChartData>,
    change: Float,
    isColorDialogOpen: Boolean,
    selectedTimeInterval: TimeInterval,
    onAccountNameChange: (String) -> Unit,
    onBankNameChange: (String) -> Unit,
    onCurrentBalanceChange: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    toggleColorDialog: (Boolean) -> Unit,
    onTimeIntervalChange: (TimeInterval) -> Unit,
    onSaveClick: () -> Unit,
    onDelete: () -> Unit
) {
    val formattedBalance = remember(currentBalance) { formatAmount(currentBalance.toFloat()) }
    val formattedChange = remember(change) { formatAmount(change) }

    val editEnabled = remember { mutableStateOf(false) }
    val restoreColor = remember { mutableStateOf(colors[0]) }

    if (isColorDialogOpen) {
        RallyAlertDialog(
            onDismiss = {
                onColorChange(restoreColor.value) // Don't save if the user dismisses the dialog
                toggleColorDialog(false)
            },
            onConfirm = { toggleColorDialog(false) },
            confirmContent = {
                Text(text = "SPARA", color = MaterialTheme.colors.primary)
            },
            title = { Text(text = "Välj en färg") }
        ) {
            Column(Modifier.fillMaxWidth()) {
                ColorPicker(
                    modifier = Modifier.fillMaxWidth(),
                    items = colors,
                    selectedColor = color,
                    onColorSelected = onColorChange
                )
            }
        }
    }

    Column(
        Modifier
            .navigationBarsWithImePadding()
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = accountName, style = MaterialTheme.typography.h6)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = "($bank)", style = MaterialTheme.typography.subtitle1)
                }
            }
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)
            ) {
                Text(text = "RADERA")
            }
        }


        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = "Kapital", style = MaterialTheme.typography.body2)
                }
                Text(text = "$formattedBalance kr", style = MaterialTheme.typography.h6)
            }
            Column(horizontalAlignment = Alignment.End) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = "Utveckling", style = MaterialTheme.typography.body2)
                }
                // FIXME: Vet ej om kr eller procent eller båda
                Text(text = "$formattedChange kr", style = MaterialTheme.typography.h6)
            }

        }
        Spacer(modifier = Modifier.height(24.dp))

        RallyLineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 12.dp),
            color = color,
            lineChartData = history
        )

        RallyChipGroup(
            modifier = Modifier.padding(bottom = 12.dp),
            items = timeIntervals,
            selectedItem = selectedTimeInterval,
            selectedItemString = { timeInterval -> timeInterval.value },
            selectedItemColor = color,
            onItemSelected = onTimeIntervalChange
        )

        // FIXME: This should have it's own state to not update the rest of the ui when it changes
        // FIXME: So currentBalance should be changed back to Float
        // FIXME: And snackbar should only be displayed when onSaveClick is called
        SavingsCreateEditCard(
            enabled = editEnabled.value,
            restoreColor = restoreColor,
            cardLabel = "Redigera",
            button = {
                IconButton(onClick = { editEnabled.value = !editEnabled.value }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit account")
                }
            },
            accountName = accountName,
            bank = bank,
            currentBalance = currentBalance,
            color = color,
            onAccountNameChange = onAccountNameChange,
            onBankNameChange = onBankNameChange,
            onCurrentBalanceChange = onCurrentBalanceChange,
            toggleDialog = toggleColorDialog,
            onSaveClick = onSaveClick
        )
    }

}

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

