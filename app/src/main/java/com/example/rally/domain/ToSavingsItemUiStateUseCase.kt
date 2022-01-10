package com.example.rally.domain

import com.example.rally.model.SavingsAccount
import com.example.rally.model.SavingsAccountData
import com.example.rally.ui.components.linechart.LineChartData
import com.example.rally.ui.savings.SavingsItemUiState
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.roundToInt

class ToSavingsItemUiStateUseCase @Inject constructor(
    private val hexToColorUseCase: HexToColorUseCase,
) {
    operator fun invoke(account: SavingsAccount): SavingsItemUiState {
        return SavingsItemUiState(
                accountId = account.id,
                accountName = account.name,
                bank = account.bank,
                currentBalance = account.balance.last().amount.roundToInt().toString(), // Replace with use case
                color = hexToColorUseCase(account.colorHEX),
                history = account.balance.map { (date, amount) ->
                    LineChartData(
                        timeStamp = date.toInstant().toEpochMilli(),
                        amount = amount
                    )
                },
                change = account.balance.last().amount - account.balance.first().amount
            )
    }
}

class FromSavingsItemUiStateUseCase @Inject constructor(
    private val colorToHexUseCase: ColorToHexUseCase
) {
    operator fun invoke(itemUiState: SavingsItemUiState): SavingsAccount {
        return SavingsAccount(
            id = itemUiState.accountId,
            name = itemUiState.accountName,
            bank = itemUiState.bank,
            colorHEX = colorToHexUseCase(itemUiState.color),
            balance = itemUiState.history.map { (dateLong, amount) ->
                SavingsAccountData(
                    date = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(dateLong), ZoneId.systemDefault()
                    ),
                    amount = amount
                )
            }
        )
    }
}

