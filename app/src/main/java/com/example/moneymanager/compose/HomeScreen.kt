package com.example.moneymanager.compose

import android.view.SurfaceControl
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneymanager.R
import java.text.DecimalFormat

@Composable
fun HomeScreen() {
    val transactions = remember {
        mutableStateListOf<Transaction>()
    }

    var dialogState by remember {
        mutableStateOf<TransactionDialogState>(TransactionDialogState.Closed)
    }

    val balance =
        transactions.filter { it.isIncome }.sumOf { it.sum } - transactions.filter { !it.isIncome }
            .sumOf { it.sum }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.mainBackground)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Актуальный баланс", color = colorResource(id = R.color.mainTextColor))
        Text(
            text = "${balance.formatToDecimalValue()} $",
            color = colorResource(id = R.color.mainTextColor),
            fontSize = 60.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoundedButton(onClick = {
                dialogState = TransactionDialogState.Opened(isIncome = true)
            }, text = "+")
            RoundedButton(onClick = {
                dialogState = TransactionDialogState.Opened(isIncome = false)
            }, text = "-")
            RoundedButton(onClick = { transactions.clear() }, text = "AC", fontSize = 25.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp)
                .background(
                    color = colorResource(id = R.color.buttonColor),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(transactions.toList()) { transaction ->
                TransactionItem(transaction)
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
    }

    if (dialogState != TransactionDialogState.Closed) {
        TransactionDialog(
            onDismissRequest = { dialogState = TransactionDialogState.Closed },
            isIncome = (dialogState as TransactionDialogState.Opened).isIncome,
            balance = balance,
            onAddTransaction = {
                transactions.add(it)
                dialogState = TransactionDialogState.Closed
            })
    }
}

@Immutable
sealed class TransactionDialogState {
    data class Opened(val isIncome: Boolean) : TransactionDialogState()
    data object Closed : TransactionDialogState()
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = transaction.name,
            fontSize = 25.sp,
            color = colorResource(id = R.color.mainTextColor)
        )
        Text(
            text = (if (!transaction.isIncome) "-" else "+").plus(transaction.sum.formatToDecimalValue()),
            color = if (transaction.isIncome) Color.Green else Color.Red,
            fontSize = 25.sp
        )
    }
}

fun Double.formatToDecimalValue() = DecimalFormat("0.00").format(this)


@Composable
fun RoundedButton(
    onClick: () -> Unit,
    text: String,
    fontSize: TextUnit = 45.sp
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(width = 90.dp, height = 90.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(
                id = R.color.buttonColor
            ), contentColor = colorResource(id = R.color.mainTextColor)
        )
    ) {
        Text(text = text, fontSize = fontSize)

    }
}

@Composable
fun TransactionDialog(
    onDismissRequest: () -> Unit,
    isIncome: Boolean,
    balance: Double,
    onAddTransaction: (Transaction) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        val context = LocalContext.current

        var transactionName by remember {
            mutableStateOf("")
        }

        var transactionSum by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier.background(color = colorResource(id = R.color.mainBackground)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = transactionName,
                onValueChange = { transactionName = it },
                placeholder = {
                    Text(
                        text = "Название транзакции",
                        color = colorResource(id = R.color.mainTextColor)
                    )
                },
                colors = TextFieldDefaults.textFieldColors(textColor = colorResource(id = R.color.mainTextColor))
            )
            TextField(
                value = transactionSum,
                onValueChange = { transactionSum = it },
                placeholder = {
                    Text(
                        text = "Сумма транзакции",
                        color = colorResource(id = R.color.mainTextColor)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                colors = TextFieldDefaults.textFieldColors(textColor = colorResource(id = R.color.mainTextColor))
            )
            Button(
                onClick = {
                    when {
                        (transactionName.trim().isEmpty()) ->
                            Toast.makeText(
                                context,
                                "Введите название транзакции!",
                                Toast.LENGTH_SHORT
                            ).show()

                        (transactionSum.trim().toDoubleOrNull() == null) ->
                            Toast.makeText(
                                context,
                                "Некорректный формат суммы!",
                                Toast.LENGTH_SHORT
                            ).show()

                        (!isIncome && transactionSum.toDouble() > balance) ->
                            Toast.makeText(
                                context,
                                "Сумма расходов больше баланса!",
                                Toast.LENGTH_SHORT
                            ).show()

                        else -> onAddTransaction(
                            Transaction(
                                transactionName.trim(),
                                isIncome,
                                transactionSum.trim().toDouble()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.mainBackground),
                    contentColor = colorResource(
                        id = R.color.mainTextColor
                    )
                )
            ) {
                Text(text = "Сохранить")
            }
        }
    }
}

data class Transaction(
    val name: String,
    val isIncome: Boolean,
    val sum: Double
)

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Preview
@Composable
fun TransactionDialogPreview() {
    TransactionDialog({}, true, 0.0, {})
}