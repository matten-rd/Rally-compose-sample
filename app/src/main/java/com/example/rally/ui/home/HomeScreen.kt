package com.example.rally.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rally.ui.components.*
import com.example.rally.ui.savings.ISavingsUiState
import com.example.rally.ui.savings.SavingsViewModel


@Composable
fun HomeScreen(
    savingsViewModel: SavingsViewModel = hiltViewModel(),
    navigateToSavingsScreen: () -> Unit,
    navigateToEditScreen: (Int) -> Unit
) {
    val savingsUiState by savingsViewModel.uiState.collectAsState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        LoadingErrorContent(
            empty = when (savingsUiState) {
                is ISavingsUiState.HasSavingsAccounts -> false
                is ISavingsUiState.NoSavingsAccounts -> savingsUiState.isLoading
            },
            emptyContent = {
                HomeScreenAlt(
                    cardLabel = "Sparande",
                    onClick = navigateToSavingsScreen
                ) { FullScreenLoading() }
            },
            error = savingsUiState.isError,
            errorContent = {
                HomeScreenAlt(
                    cardLabel = "Sparande",
                    onClick = navigateToSavingsScreen
                ) { Text(text = "Ett fel har inträffat.") }
            },
            loading = savingsUiState.isLoading,
            onRefresh = { savingsViewModel.fetchSavings() }
        ) {
            HomeScreenSavingsContent(
                uiState = savingsUiState,
                navigateToSavingsScreen = navigateToSavingsScreen,
                navigateToEditScreen = navigateToEditScreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LoadingErrorContent(
            empty = when (savingsUiState) {
                is ISavingsUiState.HasSavingsAccounts -> false
                is ISavingsUiState.NoSavingsAccounts -> savingsUiState.isLoading
            },
            emptyContent = {
                HomeScreenAlt(
                    cardLabel = "Transaktioner",
                    onClick = navigateToSavingsScreen
                ) { FullScreenLoading() }
            },
            error = savingsUiState.isError,
            errorContent = {
                HomeScreenAlt(
                    cardLabel = "Transaktioner",
                    onClick = navigateToSavingsScreen
                ) { Text(text = "Ett fel har inträffat.") }
            },
            loading = savingsUiState.isLoading,
            onRefresh = { savingsViewModel.fetchSavings() }
        ) {
            HomeScreenSavingsContent(
                uiState = savingsUiState,
                navigateToSavingsScreen = navigateToSavingsScreen,
                navigateToEditScreen = navigateToEditScreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

    }
}

@Composable
fun HomeScreenSavingsContent(
    uiState: ISavingsUiState,
    navigateToSavingsScreen: () -> Unit,
    navigateToEditScreen: (Int) -> Unit
) {
    when (uiState) {
        is ISavingsUiState.HasSavingsAccounts -> {
            val savingsAccounts = uiState.savingsItems
            RallyCard(
                items = savingsAccounts,
                colors = { savingsAccount -> savingsAccount.color },
                amounts = { savingsAccount -> savingsAccount.currentBalance.toFloat() },
                cardLabel = "Sparande",
                currentAmount = savingsAccounts.sumOfFloat { it.currentBalance.toFloat() },
                showAll = false,
                button = {
                    TextButton(onClick = navigateToSavingsScreen) {
                        Text(text = "VISA MER")
                    }
                },
            ) { savingsAccount ->
                BaseRow(
                    color = savingsAccount.color,
                    title = savingsAccount.accountName,
                    subtitle = savingsAccount.bank,
                    amount = savingsAccount.currentBalance.toFloat(),
                    onClick = { navigateToEditScreen(savingsAccount.accountId) }
                )
            }
        }
        is ISavingsUiState.NoSavingsAccounts -> {
            HomeScreenAlt(
                cardLabel = "Sparande",
                onClick = navigateToSavingsScreen
            ) { Text(text = "Ingen data att visa.") }
        }
    }

}

@Composable
private fun HomeScreenAlt(
    modifier: Modifier = Modifier,
    cardLabel: String,
    onClick: () -> Unit,
    alt: @Composable () -> Unit
) {
    Column(modifier) {
        RallyCard<Nothing>(
            cardLabel = cardLabel,
            alt = { alt() },
            currentAmount = 0f,
            showAll = false,
            button = {
                TextButton(onClick = onClick) {
                    Text(text = "Visa mer")
                }
            }
        )
    }
}

