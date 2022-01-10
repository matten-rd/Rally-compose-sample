package com.example.rally.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
internal fun SavingsCreateEditCard(
    enabled: Boolean = true,
    restoreColor: MutableState<Color>,
    cardLabel: String,
    button: @Composable () -> Unit = {},
    accountName: String,
    bank: String,
    currentBalance: String,
    color: Color,
    onAccountNameChange: (String) -> Unit,
    onBankNameChange: (String) -> Unit,
    onCurrentBalanceChange: (String) -> Unit,
    toggleDialog: (Boolean) -> Unit,
    onSaveClick: () -> Unit
) {
    RallyCard<Nothing>(
        cardLabel = cardLabel,
        button = button,
        alt = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RallyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = accountName,
                    onValueChange = { onAccountNameChange(it) },
                    label = "Namn på sparkontot",
                    imeAction = ImeAction.Next,
                    enabled = enabled
                )
                RallyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = bank,
                    onValueChange = { onBankNameChange(it) },
                    label = "Sparplattform",
                    imeAction = ImeAction.Next,
                    enabled = enabled
                )
                RallyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = currentBalance,
                    onValueChange = { onCurrentBalanceChange(it) },
                    label = "Pengar på kontot just nu",
                    keyboardType = KeyboardType.Number,
                    enabled = enabled
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        restoreColor.value = color
                        toggleDialog(true)
                    },
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        tint = color
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Välj färg")
                }


                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled
                ) {
                    Text(text = "Spara")
                }
            }
        }
    )
}