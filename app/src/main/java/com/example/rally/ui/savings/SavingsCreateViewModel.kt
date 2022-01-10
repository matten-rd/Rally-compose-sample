package com.example.rally.ui.savings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rally.data.repositories.SavingsRepository
import com.example.rally.domain.ColorToHexUseCase
import com.example.rally.model.SavingsAccount
import com.example.rally.model.SavingsAccountData
import com.example.rally.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class SavingsCreateViewModel @Inject constructor(
    private val colorToHexUseCase: ColorToHexUseCase,
    private val savingsRepository: SavingsRepository
) : ViewModel() {

    val colorsOld = listOf(
        Green50, Green100, Green200, Green300, Green400,
        Green500, Green600, Green700, Green800, Green900,
        DarkGreen50, DarkGreen100, DarkGreen200, DarkGreen300, DarkGreen400,
        DarkGreen500, DarkGreen600, DarkGreen700, DarkGreen800, DarkGreen900
    )

    // FIXME: This should be moved up one level (to useCase)
    val colors = listOf(
        Green300, Green500, Green700, DarkGreen700,
        Purple50, Purple200, Purple400, Purple700,
        Blue100, Blue200, Blue500, Blue700
    )

    var accountNameInput by mutableStateOf<String>("")
        private set

    var bankNameInput by mutableStateOf<String>("")
        private set

    var currentBalanceInput by mutableStateOf<String>("")
        private set

    var colorInput by mutableStateOf<Color>(colors[0])
        private set

    fun onAccountNameChange(newAccountName: String) {
        accountNameInput = newAccountName
    }
    fun onBankNameChange(newBankName: String) {
        bankNameInput = newBankName
    }
    fun onCurrentBalanceChange(newCurrentBalance: String) {
        currentBalanceInput = newCurrentBalance
    }
    fun onColorChange(newColor: Color) {
        colorInput = newColor
    }

    var isDialogOpen by mutableStateOf(false)
        private set

    fun toggleDialog(open: Boolean) {
        isDialogOpen = open
    }

    fun onSaveClick() {
        // FIXME: Don't default to 0f, display error instead
        val account = SavingsAccount(
            name = accountNameInput,
            bank = bankNameInput,
            colorHEX = colorToHexUseCase(colorInput),
            balance = listOf(
                SavingsAccountData(
                    date = OffsetDateTime.now(),
                    amount = currentBalanceInput.toFloatOrNull() ?: 0f
                )
            )
        )

        viewModelScope.launch {
            createAccount(account)
        }
    }

    private suspend fun createAccount(account: SavingsAccount) {
        savingsRepository.insertAccount(account)
        eventChannel.send(CreateEvent.AccountCreated)
    }

    sealed class CreateEvent {
        object AccountCreated : CreateEvent()
    }

    private val eventChannel = Channel<CreateEvent>()
    val events = eventChannel.receiveAsFlow()



}