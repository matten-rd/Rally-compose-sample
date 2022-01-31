package com.example.rally.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

/**
 * Store filtered history here to not change the source of truth
 */
class HistoryState(
    private val history: List<LineChartData>,
    private val selectedTimeInterval: TimeInterval
) {
    // The filtered history that is displayed
    // Changes when the user selects new TimeInterval
    val filteredHistory = history.filter {
        it.timeStamp > selectedTimeInterval.date
    }
}

@Composable
fun rememberHistoryState(
    history: List<LineChartData>,
    selectedTimeInterval: TimeInterval
) = remember(selectedTimeInterval) {
    // Update state when user selects new TimeInterval
    HistoryState(history, selectedTimeInterval)
}

/**
 * Main detail page of an account
 *
 * @param scaffoldState used to display snackbars
 * @param viewModel [SavingsDetailViewModel] handle logic
 * @param onNavigateBack event to navigate back
 */
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
            empty = if (account != null) false else uiState.isLoading,
            emptyContent = { FullScreenLoading(Modifier.fillMaxSize()) },
            error = uiState.isError,
            errorContent = { FullScreenError() {  } },
            loading = uiState.isLoading,
            onRefresh = { viewModel.fetchAccount(isRefresh = true) }
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
                val historyState = rememberHistoryState(account.history, uiState.selectedTimeInterval)
                SavingsScreenDetailContent(
                    colors = viewModel.colors,
                    timeIntervals = viewModel.allTimeIntervals,
                    accountName = account.accountName,
                    bank = account.bank,
                    currentBalance = account.currentBalance,
                    color = account.color,
                    history = historyState.filteredHistory,
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
    // The balance and change on screen should never be recalculated
    val formattedBalance = rememberSaveable { formatAmount(currentBalance.toFloatOrNull()) }
    val formattedChange = remember { formatAmount(change) }

    val editEnabled = rememberSaveable { mutableStateOf(false) }
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
                // FIXME: Not sure if I should display in percent or absolute or both
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


