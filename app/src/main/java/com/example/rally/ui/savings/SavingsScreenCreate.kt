package com.example.rally.ui.savings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rally.ui.components.ColorPicker
import com.example.rally.ui.components.RallyAlertDialog
import com.example.rally.ui.components.SavingsCreateEditCard
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.flow.collect

@Composable
fun SavingsCreateScreen(
    viewModel: SavingsCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SavingsCreateViewModel.CreateEvent.AccountCreated -> onNavigateBack()
            }
        }
    }

    SavingsCreateScreenContent(
        colors = viewModel.colors,
        accountName = viewModel.accountNameInput,
        bank = viewModel.bankNameInput,
        currentBalance = viewModel.currentBalanceInput,
        color = viewModel.colorInput,
        isDialogOpen = viewModel.isDialogOpen,
        onAccountNameChange = { viewModel.onAccountNameChange(it) },
        onBankNameChange = { viewModel.onBankNameChange(it) },
        onCurrentBalanceChange = { viewModel.onCurrentBalanceChange(it) },
        onColorChange = { viewModel.onColorChange(it) },
        toggleDialog = { viewModel.toggleDialog(it) },
        onSaveClick = { viewModel.onSaveClick() }
    )
}


@Composable
private fun SavingsCreateScreenContent(
    colors: List<Color>,
    accountName: String,
    bank: String,
    currentBalance: String,
    color: Color,
    isDialogOpen: Boolean,
    onAccountNameChange: (String) -> Unit,
    onBankNameChange: (String) -> Unit,
    onCurrentBalanceChange: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    toggleDialog: (Boolean) -> Unit,
    onSaveClick: () -> Unit
) {
    val restoreColor = remember { mutableStateOf(colors[0]) }

    if (isDialogOpen) {
        RallyAlertDialog(
            onDismiss = {
                onColorChange(restoreColor.value) // Don't save if the user dismisses the dialog
                toggleDialog(false)
            },
            onConfirm = { toggleDialog(false) },
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
            .padding(12.dp)
    ) {
        SavingsCreateEditCard(
            restoreColor = restoreColor,
            cardLabel = "Skapa konto",
            accountName = accountName,
            bank = bank,
            currentBalance = currentBalance,
            color = color,
            onAccountNameChange = onAccountNameChange,
            onBankNameChange = onBankNameChange,
            onCurrentBalanceChange = onCurrentBalanceChange,
            toggleDialog = toggleDialog,
            onSaveClick = onSaveClick
        )
    }
}
