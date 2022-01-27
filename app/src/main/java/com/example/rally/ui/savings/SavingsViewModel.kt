package com.example.rally.ui.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rally.data.Result
import com.example.rally.data.repositories.SavingsRepository
import com.example.rally.domain.ToSavingsItemUiStateUseCase
import com.example.rally.util.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


private data class SavingsViewModelState(
    val savingsItems: List<SavingsItemUiState>? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val userMessages: List<UserMessage> = emptyList()
) {
    fun toUiState() : ISavingsUiState =
        if (savingsItems.isNullOrEmpty()) {
            ISavingsUiState.NoSavingsAccounts(
                isLoading = isLoading,
                isError = isError,
                userMessages = userMessages,
            )
        } else {
            ISavingsUiState.HasSavingsAccounts(
                savingsItems = savingsItems,
                isLoading = isLoading,
                isError = isError,
                userMessages = userMessages
            )
        }
}


@HiltViewModel
class SavingsViewModel @Inject constructor(
    private val toSavingsItemUiStateUseCase: ToSavingsItemUiStateUseCase,
    private val savingsRepository: SavingsRepository
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SavingsViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            viewModelState.value.toUiState()
        )

    private var fetchJob: Job? = null

    init {
        fetchSavings()
    }

    fun fetchSavings(isRefresh: Boolean = false) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            viewModelState.update { it.copy(isLoading = true) }
            if (isRefresh) {
                delay(800) // Add delay to enhance user experience
            }
            savingsRepository.getAllAccounts().collect { result ->
                viewModelState.update {
                    when(result) {
                        is Result.Success -> {
                            try {
                                val savingsListUiItems = result.data.map { account -> toSavingsItemUiStateUseCase(account) }
                                it.copy(savingsItems = savingsListUiItems, isLoading = false, isError = false)
                            } catch (e: Exception) {
                                val errorMessages = it.userMessages + getMessagesFromThrowable(
                                    Exception("Unexpected error"))
                                it.copy(userMessages = errorMessages, isLoading = false, isError = true)
                            }
                        }
                        is Result.Error -> {
                            val errorMessages = it.userMessages + getMessagesFromThrowable(result.exception)
                            it.copy(userMessages = errorMessages, isLoading = false, isError = true)
                        }
                    }.exhaustive
                }
            }
        }
    }

    private fun getMessagesFromThrowable(e: Exception): List<UserMessage> {
        // TODO: Return error messages
        val msg = e.message ?: "Unknown error"
        return listOf(UserMessage(UUID.randomUUID().mostSignificantBits, msg))
    }


    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.userMessages.filterNot { it.id == errorId }
            currentUiState.copy(userMessages = errorMessages)
        }
    }


}