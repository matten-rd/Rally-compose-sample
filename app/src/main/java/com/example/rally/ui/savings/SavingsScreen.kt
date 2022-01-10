package com.example.rally.ui.savings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rally.ui.components.*


@ExperimentalAnimationApi
@Composable
fun SavingsScreen(
    scaffoldState: ScaffoldState,
    navigateToCreateScreen: () -> Unit,
    navigateToEditScreen: (Int) -> Unit,
    viewModel: SavingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // If HasSavings -> Never display FullScreenLoading
    // If NoSavings -> Display FullScreenLoading if it's still loading else display content
    // If error -> Display FullScreenError
    // If not error or loading -> Display content
    Column(Modifier.fillMaxSize()) {
        LoadingErrorContent(
            empty = when (uiState) {
                is ISavingsUiState.HasSavingsAccounts -> false
                is ISavingsUiState.NoSavingsAccounts -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading(Modifier.fillMaxSize()) },
            error = uiState.isError,
            errorContent = { FullScreenError { viewModel.fetchSavings() } },
            loading = uiState.isLoading,
            onRefresh = { viewModel.fetchSavings() }
        ) {
            SavingsScreenContent(
                uiState = uiState,
                navigateToCreateScreen = navigateToCreateScreen,
                navigateToEditScreen = navigateToEditScreen
            )
        }
    }

    // Handle snackbar messages
    SnackbarMessageHandler(
        uiState = uiState,
        scaffoldState = scaffoldState,
        onRefresh = { viewModel.fetchSavings() },
        onErrorDismiss = { viewModel.errorShown(it) }
    )

}


@ExperimentalAnimationApi
@Composable
private fun SavingsScreenContent(
    uiState: ISavingsUiState,
    navigateToCreateScreen: () -> Unit,
    navigateToEditScreen: (Int) -> Unit,
) {
    // If this Composable is entered no error can/has occur
    when (uiState) {
        is ISavingsUiState.HasSavingsAccounts -> {
            // Guarantees that savingsItems is not null or empty
            val savingsAccounts = uiState.savingsItems
            StatementBody(
                items = savingsAccounts,
                colors = { savingsAccount -> savingsAccount.color },
                amounts = { savingsAccount -> savingsAccount.currentBalance.toFloat() },
                totalAmount = savingsAccounts.sumOfFloat { it.currentBalance.toFloat() },
                circleLabel = "Totalt sparande",
                cardLabel = "Sparkonton",
                buttonText = "Nytt sparkonto",
                onClick = navigateToCreateScreen
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
            // savingsItems is either null or empty
            SavingsScreenNoAccounts(
                cardLabel = "Sparkonton",
                buttonText = "Nytt sparkonto",
                onClick = navigateToCreateScreen,
            )
        }
    }
}

@Composable
fun SavingsScreenNoAccounts(
    cardLabel: String,
    buttonText: String,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier
        .padding(12.dp)
        .verticalScroll(rememberScrollState()) // For SwipeRefresh to work
    ) {
        Box(
            Modifier
                .padding(12.dp)
                .height(250.dp)
                .fillMaxWidth()
        ) {
            // TODO: Design a better no accounts graphic
            Text(text = "Skapa ett nytt konto")
        }
        Spacer(Modifier.height(12.dp))

        RallyCard<Nothing>(
            cardLabel = cardLabel,
            button = {
                TextButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = buttonText)
                }
            },
            showAll = false
        )
    }
}

@Composable
fun SavingsScreenErrorHandler(
    uiState: ISavingsUiState,
    scaffoldState: ScaffoldState,
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
        val retryMessageText = "Uppdatera"

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
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}