package com.example.moneymanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.compose.HomeScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Фрагмент для управления балансом и транзакциями в приложении.
 */
class HomeFragment : Fragment() {

    // Переменные для работы с данными и интерфейсом.
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var balanceTextView: TextView
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    // Переопределение метода для создания и настройки пользовательского интерфейса фрагмента.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HomeScreen()
            }
        }
    }

    // Обновление отображаемого баланса.
    @SuppressLint("StringFormatMatches")
    private fun updateBalance() {
        val balance = sharedPreferences.getFloat("balance", 0.0f)
        balanceTextView.text = getString(R.string.balance, balance)
    }

    // Сохранение баланса в SharedPreferences.
    private fun saveBalance(balance: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("balance", balance)
        editor.apply()
        updateBalance()
    }

    // Добавление новой транзакции.
    fun addTransaction(isIncome: Boolean, amount: Float, name: String) {
        val balance = sharedPreferences.getFloat("balance", 0.0f)

        // Проверка наличия достаточного баланса перед выполнением расхода.
        if (!isIncome && balance - amount < 0) {
            Toast.makeText(
                requireContext(),
                "Оказывается так нельзя",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Определение типа транзакции (доход или расход) и форматирование текущей даты.
        val transactionType = if (isIncome) "Доход" else "Расход"
        val currentDate =
            SimpleDateFormat("\ndd.MM.yy | HH:mm", Locale.getDefault()).format(Date())

        // Создание строки для новой транзакции, включая сумму, описание и дату.
        val transactionSeparator = "———————————————————————————"
        val transaction = "$transactionType: $amount - $name $currentDate\n$transactionSeparator"

        // Извлечение списка транзакций из SharedPreferences и преобразование в изменяемый список строк.
        val transactionsList =
            sharedPreferences.getString("transactions", "")?.split("\n")?.toMutableList()

        // Добавление новой транзакции к списку.
        transactionsList?.add(transaction)

        // Преобразование списка транзакций в строку с разделителями новой строки.
        val transactions = transactionsList?.joinToString("\n") ?: ""

        // Вычисление нового баланса после добавления транзакции.
        val newBalance = if (isIncome) {
            balance + amount
        } else {
            balance - amount
        }

        // Сохранение нового баланса.
        saveBalance(newBalance)

        // Сохранение обновленного списка транзакций.
        val editor = sharedPreferences.edit()
        editor.putString("transactions", transactions)
        editor.apply()

        // Отображение обновленного списка транзакций.
        displayTransactions()
    }

    // Отображение списка транзакций в RecyclerView.
    private fun displayTransactions() {
        // Извлечение строки с транзакциями из SharedPreferences.
        val transactions = sharedPreferences.getString("transactions", "")

        // Инициализация адаптера для RecyclerView с использованием списка транзакций.
        transactionAdapter = TransactionAdapter(transactions?.split("\n").orEmpty())

        // Установка адаптера для RecyclerView.
        transactionsRecyclerView.adapter = transactionAdapter
    }

    // Отображение диалогового окна для ввода данных транзакции.
    private fun showTransactionDialog(isIncome: Boolean) {
        // Надувание макета диалогового окна из XML-файла.
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_transaction, null)

        // Инициализация элементов интерфейса в диалоговом окне.
        val amountEditText: EditText = dialogView.findViewById(R.id.amountEditText)
        val nameEditText: EditText = dialogView.findViewById(R.id.nameEditText)
        val saveButton: Button = dialogView.findViewById(R.id.saveButton)

        // Настройка параметров диалогового окна, включая его заголовок.
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (isIncome) "Доход" else "Расход")

        // Создание объекта AlertDialog на основе настроек.
        val alertDialog = dialogBuilder.create()

        // Установка фона для диалогового окна.
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.background)

        // Установка слушателя событий на кнопку "Сохранить" в диалоговом окне.
        saveButton.setOnClickListener {
            // Извлечение введенных пользователем данных из текстовых полей.
            val amountStr = amountEditText.text.toString()
            val name = nameEditText.text.toString()

            // Проверка наличия введенной суммы.
            if (amountStr.isEmpty()) {
                // Отображение всплывающего уведомления о неверных данных.
                Toast.makeText(
                    requireContext(),
                    "Извините, но воздух не считается цифрой",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (name.isEmpty()) {
                // Отображение всплывающего уведомления о неверных данных.
                Toast.makeText(
                    requireContext(),
                    "Понимаю, мне тоже лень его заполнять",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Если данные введены правильно:
                val amount = amountStr.toFloat()
                // Конвертация суммы в тип Float и вызов функции добавления транзакции.
                addTransaction(isIncome, amount, name)
                // Закрытие диалогового окна после сохранения данных.
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    // Очистка списка транзакций и обнуление баланса.
    private fun clearTransactionsAndBalance() {
        // Очистка списка транзакций в SharedPreferences.
        val editor = sharedPreferences.edit()
        editor.putString("transactions", "")
        editor.apply()
        // Обнуление баланса.
        saveBalance(0.0f)
        // Отображение обновленного списка транзакций.
        displayTransactions()
    }
}