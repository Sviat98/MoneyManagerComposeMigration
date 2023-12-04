package com.example.moneymanager

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var balanceTextView: TextView
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        balanceTextView = view.findViewById(R.id.balanceTextView)
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)

        transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        updateBalance()

        val incomeButton: Button = view.findViewById(R.id.incomeButton)
        val expenseButton: Button = view.findViewById(R.id.expenseButton)

        incomeButton.setOnClickListener { showTransactionDialog(true) }
        expenseButton.setOnClickListener { showTransactionDialog(false) }

        displayTransactions()

        return view
    }

    private fun updateBalance() {
        val balance = sharedPreferences.getFloat("balance", 0.0f)
        balanceTextView.text = getString(R.string.balance, balance)
    }

    private fun saveBalance(balance: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("balance", balance)
        editor.apply()
        updateBalance()
    }

    fun addTransaction(isIncome: Boolean, amount: Float, name: String) {
        val balance = sharedPreferences.getFloat("balance", 0.0f)

        if (!isIncome && balance - amount < 0) {
            Toast.makeText(
                requireContext(),
                "Оказывается так нельзя",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val transactionType = if (isIncome) "Доход" else "Расход"
        val currentDate =
            SimpleDateFormat("\ndd.MM.yy | HH:mm", Locale.getDefault()).format(Date())

        val transactionSeparator = "———————————————————————————"
        val transaction = "$transactionType: $amount - $name $currentDate\n$transactionSeparator"

        val transactionsList =
            sharedPreferences.getString("transactions", "")?.split("\n")?.toMutableList()
        transactionsList?.add(transaction)

        val transactions = transactionsList?.joinToString("\n") ?: ""

        val newBalance = if (isIncome) {
            balance + amount
        } else {
            balance - amount
        }

        saveBalance(newBalance)

        val editor = sharedPreferences.edit()
        editor.putString("transactions", transactions)
        editor.apply()

        displayTransactions()
    }


    private fun displayTransactions() {
        val transactions = sharedPreferences.getString("transactions", "")
        transactionAdapter = TransactionAdapter(transactions?.split("\n").orEmpty())
        transactionsRecyclerView.adapter = transactionAdapter
    }

    private fun showTransactionDialog(isIncome: Boolean) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_transaction, null)

        val amountEditText: EditText = dialogView.findViewById(R.id.amountEditText)
        val nameEditText: EditText = dialogView.findViewById(R.id.nameEditText)
        val saveButton: Button = dialogView.findViewById(R.id.saveButton)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (isIncome) "Доход" else "Расход")

        val alertDialog = dialogBuilder.create()

        alertDialog.window?.setBackgroundDrawableResource(R.drawable.background)

        saveButton.setOnClickListener {
            val amountStr = amountEditText.text.toString()
            val name = nameEditText.text.toString()

            if (amountStr.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Извините, но воздух не считается цифрой",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (name.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Понимаю, мне тоже лень его заполнять",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val amount = amountStr.toFloat()
                    addTransaction(isIncome, amount, name)
                    alertDialog.dismiss()
                }
            }
            alertDialog.show()
        }
    }