package com.example.moneymanager

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.DecimalFormat

@Composable
fun HomeScreen() {
    val transactions = remember {
        mutableStateListOf<Transaction>().apply {
            add(Transaction("зарплата", 100.0))
            add(Transaction("мороженое", -20.0))
        }
    }


    var dialogState by remember {
        mutableStateOf<TransactionDialogState>(TransactionDialogState.Closed)
    }


    val balance by remember {
        derivedStateOf { transactions.sumOf { it.summa }.formatToDecimalValue() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.mainBackground))
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Актуальный баланс", color = colorResource(id = R.color.mainTextColor))
        Text(
            text = "$balance $",
            color = colorResource(id = R.color.mainTextColor),
            fontSize = 60.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoundedButton(onClick = {
                dialogState = TransactionDialogState.Income
            }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    modifier = Modifier.size(50.dp),
                    contentDescription = null
                )
            }
            RoundedButton(onClick = {
                dialogState = TransactionDialogState.Outcome
            }) {
                Text(text = "-", fontSize = 60.sp)
            }
            RoundedButton(onClick = { transactions.clear() }) {
                Text(text = "AC", fontSize = 40.sp)
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = colorResource(id = R.color.buttonColor),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(
                    name = transaction.name,
                    summa = transaction.summa
                )
            }
        }
    }

    if (dialogState !is TransactionDialogState.Closed) {
        TransactionDialog(
            onDismissRequest = { dialogState = TransactionDialogState.Closed },
            dialogState = dialogState,
            onTransactionAdd = {transactions.add(it)}
        )
    }
}

@Composable
fun TransactionItem(
    name: String = "",
    summa: Double = 1.0
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = colorResource(id = R.color.mainTextColor), fontSize = 24.sp)
        Text(
            summa.formatToDecimalValue(),
            fontSize = 24.sp,
            color = if (summa < 0.0) Color.Red else Color.Green
        )
    }
}

sealed class TransactionDialogState {
    object Closed : TransactionDialogState()
    object Income: TransactionDialogState()
    object Outcome: TransactionDialogState()
    //data class Opened(val isIncome: Boolean) : TransactionDialogState()
}

@Preview
@Composable
fun TransactionItemPreview() {
    TransactionItem()
}

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    buttonContent: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(90.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.buttonColor),
            contentColor = colorResource(
                id = R.color.mainTextColor
            )
        )
    ) {
        buttonContent()
    }
}

fun Double.formatToDecimalValue() = DecimalFormat("0.00").format(this)


@Composable
fun TransactionDialog(
    onDismissRequest: () -> Unit = {},
    dialogState: TransactionDialogState = TransactionDialogState.Income,
    onTransactionAdd: (Transaction)->Unit = {}
) {

    val isIncome =  dialogState is TransactionDialogState.Income

    var name by remember {
        mutableStateOf("")
    }

    var summa by remember {
        mutableStateOf(0.0.toString())
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(color = colorResource(id = R.color.mainBackground))
        ) {
            Text(text = if (isIncome) "Добавьте доход" else "Добавьте расход", color = colorResource(id = R.color.mainTextColor))
            TextField(
                value = name,
                onValueChange = { name = it },
                colors = TextFieldDefaults.textFieldColors(textColor = colorResource(id = R.color.mainTextColor))
            )
            TextField(
                value = summa, onValueChange = { summa = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(textColor = colorResource(id = R.color.mainTextColor))
            )
            Button(onClick = {
                val transactionSum = if (!isIncome) (-1*summa.toDouble()) else summa.toDouble()
                onTransactionAdd(Transaction(name, transactionSum))
                onDismissRequest()
            }) {
                Text("Сохранить")
            }
        }

    }
}

@Preview
@Composable
fun TransactionDialogPreview() {
    TransactionDialog()
}

data class Transaction(
    val name: String,
    val summa: Double
)

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}