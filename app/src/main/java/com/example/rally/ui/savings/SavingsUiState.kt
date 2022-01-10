package com.example.rally.ui.savings

import androidx.compose.ui.graphics.Color
import com.example.rally.ui.components.linechart.LineChartData

// For errors and general messages to the user
data class UserMessage(val id: Long, val message: String)

// Holds state for the individual accounts
data class SavingsItemUiState(
    val accountId: Int,
    val accountName: String,
    val bank: String,
    val currentBalance: String,
    val color: Color,
    val history: List<LineChartData>,
    val change: Float = 0f
)

interface GeneralUiState {
    val isLoading: Boolean
    val isError: Boolean
    val userMessages: List<UserMessage>
}


sealed interface ISavingsUiState : GeneralUiState {

    override val isLoading: Boolean
    override val isError: Boolean
    override val userMessages: List<UserMessage>

    data class NoSavingsAccounts(
        override val isError: Boolean,
        override val isLoading: Boolean,
        override val userMessages: List<UserMessage>
    ) : ISavingsUiState

    data class HasSavingsAccounts(
        val savingsItems: List<SavingsItemUiState>,

        override val isError: Boolean,
        override val isLoading: Boolean,
        override val userMessages: List<UserMessage>
    ) : ISavingsUiState

}
