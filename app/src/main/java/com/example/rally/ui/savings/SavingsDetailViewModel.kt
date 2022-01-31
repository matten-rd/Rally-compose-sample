package com.example.rally.ui.savings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.rally.data.repositories.SavingsRepository
import com.example.rally.domain.FromSavingsItemUiStateUseCase
import com.example.rally.domain.ToSavingsItemUiStateUseCase
import com.example.rally.model.SavingsAccount
import com.example.rally.ui.components.linechart.LineChartData
import com.example.rally.ui.navigation.ACCOUNT_ID_KEY
import com.example.rally.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*
import javax.inject.Inject


data class SavingsDetailUiState(
    override val isLoading: Boolean = false,
    override val isError: Boolean = false,
    override val userMessages: List<UserMessage> = emptyList(),
    val savingsItem: SavingsItemUiState? = null,
    val isDialogOpen: Boolean = false,
    val selectedTimeInterval: TimeInterval = TimeInterval.MAX
) : GeneralUiState


@HiltViewModel
class SavingsDetailViewModel @Inject constructor(
    private val toSavingsItemUiStateUseCase: ToSavingsItemUiStateUseCase,
    private val fromSavingsItemUiStateUseCase: FromSavingsItemUiStateUseCase,
    private val savingsRepository: SavingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // FIXME: This should be moved up one level (to useCase)
    val colors = listOf(
        Green300, Green500, Green700, DarkGreen700,
        Purple50, Purple200, Purple400, Purple700,
        Blue100, Blue200, Blue500, Blue700
    )

    var isColorDialogOpen by mutableStateOf(false)
        private set

    fun toggleColorDialog(open: Boolean) {
        isColorDialogOpen= open
    }

    var uiState by mutableStateOf(SavingsDetailUiState())
        private set

    // Get the id from the navigation graph
    private val accountId = savedStateHandle.get<Int>(ACCOUNT_ID_KEY)

    // Use SavedStateHandle to survive process death
    private val accountLiveData =
        savedStateHandle.getLiveData<SavingsAccount>(KEY_ACCOUNT_LIVE_DATA)

    private var fetchJob: Job? = null

    init {
        // Only fill in the SavedStateHandle if it doesn't already have a Account set
        if (!savedStateHandle.contains(KEY_ACCOUNT_LIVE_DATA)) {
            fetchAccount()
        } else {
            // If the SavedStateHandle contains an Account use it directly
            val item = accountLiveData.value?.let { toSavingsItemUiStateUseCase(it) }
            uiState = if (item != null) {
                uiState.copy(isLoading = false, savingsItem = item)
            } else {
                // If the item is null then display an error message
                val errorMessages = uiState.userMessages + getMessagesFromThrowable(
                    Exception("Unexpected error"))
                uiState.copy(userMessages = errorMessages, isLoading = false, isError = true)
            }
        }
    }

    fun fetchAccount(isRefresh: Boolean = false) {
        fetchJob?.cancel()
        if (accountId != null) {
            fetchJob = viewModelScope.launch {
                uiState = uiState.copy(isLoading = true)
                if (isRefresh) {
                    delay(800)
                }
                // This will populate SavedStateHandle with the account
                accountLiveData.value = savingsRepository.getAccountById(accountId)

                accountLiveData.asFlow().collect {
                    val item = toSavingsItemUiStateUseCase(it)
                    uiState = uiState.copy(isLoading = false, savingsItem = item)
                }
            }
        } else {
            // If the id from the navigation graph is null then something went wrong
            uiState = uiState.copy(isLoading = false, isError = true)
        }

    }

    private fun getMessagesFromThrowable(e: Exception): List<UserMessage> {
        // TODO: Return error messages
        val msg = e.message ?: "Unknown error"
        return listOf(UserMessage(UUID.randomUUID().mostSignificantBits, msg))
    }

    fun toggleDialog(isDialogOpen: Boolean) {
        uiState = uiState.copy(isDialogOpen = isDialogOpen)
    }

    fun onAccountNameChange(newAccountName: String) {
        uiState = uiState.copy(
            savingsItem = uiState.savingsItem?.copy(accountName = newAccountName)
        )
    }
    fun onBankNameChange(newBankName: String) {
        uiState = uiState.copy(
            savingsItem = uiState.savingsItem?.copy(bank = newBankName)
        )
    }
    fun onCurrentBalanceChange(newCurrentBalance: String) {
        uiState = uiState.copy(
            savingsItem = uiState.savingsItem?.copy(
                currentBalance = newCurrentBalance
            )
        )
    }
    fun onColorChange(newColor: Color) {
        uiState = uiState.copy(
            savingsItem = uiState.savingsItem?.copy(color = newColor)
        )
    }
    fun onTimeIntervalChange(newTimeInterval: TimeInterval) {
        uiState = uiState.copy(selectedTimeInterval = newTimeInterval)
    }

    fun onSaveClick() {
        val account = uiState.savingsItem
        if (account != null) {
            if (isValidInput(account.currentBalance)) {
                // Add the current balance and timestamp to history
                val accountHistory = account.history + LineChartData(
                    timeStamp = Instant.now().toEpochMilli(),
                    amount = account.currentBalance.toFloat() // This is safe
                )
                // Create the updated account with new history from above
                val updatedAccount = fromSavingsItemUiStateUseCase(
                    account.copy(history = accountHistory)
                )

                viewModelScope.launch {
                    updateAccount(updatedAccount)
                }
            } else {
                // TODO: Display error on balance text field
            }
        } else {
            // FIXME: Not sure if I should send error event or update uiState instead
            // Fixme: Probably just update uiState, channel is for async (db operations etc.)
            // If the account is null then send an Error event
            viewModelScope.launch {
                eventChannel.send(DetailEvent.Error)
            }
        }
    }

    private suspend fun updateAccount(updatedAccount: SavingsAccount) {
        savingsRepository.updateAccount(updatedAccount)
        eventChannel.send(DetailEvent.AccountUpdated)
    }

    private fun isValidInput(currentBalance: String): Boolean {
        // Checks that the input is of/can be converted to Float type
        return currentBalance.toFloatOrNull() != null
    }

    fun deleteAccount() = viewModelScope.launch {
        if (accountId != null) {
            savingsRepository.deleteAccountById(accountId)
            eventChannel.send(DetailEvent.AccountDeleted)
        } else {
            eventChannel.send(DetailEvent.Error)
        }
    }

    sealed class DetailEvent {
        object AccountDeleted : DetailEvent()
        object AccountUpdated : DetailEvent()
        object Error : DetailEvent()
    }

    private val eventChannel = Channel<DetailEvent>()
    val events = eventChannel.receiveAsFlow()


    val allTimeIntervals = TimeInterval.values().toList()

}

/**
 * Store dateLabel and their corresponding date in a Long value
 */
enum class TimeInterval(val value: String, val date: Long) {
    ONE_MONTH("1m", OffsetDateTime.now().minusMonths(1L).toInstant().toEpochMilli()),
    THREE_MONTH("3m", OffsetDateTime.now().minusMonths(3L).toInstant().toEpochMilli()),
    ONE_YEAR("1 år", OffsetDateTime.now().minusYears(1L).toInstant().toEpochMilli()),
    THREE_YEAR("3 år", OffsetDateTime.now().minusYears(3L).toInstant().toEpochMilli()),
    MAX("Max", Long.MIN_VALUE)
}

private const val KEY_ACCOUNT_LIVE_DATA = "account"