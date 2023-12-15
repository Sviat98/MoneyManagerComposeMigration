package com.example.moneymanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class ConverterFragment : Fragment() {

    // Переменные так или иначе использующиеся здесь
    private lateinit var amountEditText: EditText
    private lateinit var exchangeRateEditText: EditText
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView
    private val decimalFormat: DecimalFormat

    init {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = '.'
        }
        decimalFormat = DecimalFormat("###,##0.00", symbols)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_converter, container, false)
        // Инсализация View и слушатели
        initializeViews(view)
        setupClickListener()
        setupResultTextViewClickListener()
        return view
    }

    private fun initializeViews(view: View) {
        amountEditText = view.findViewById(R.id.amountEditText)
        exchangeRateEditText = view.findViewById(R.id.exchangeRateEditText)
        convertButton = view.findViewById(R.id.convertButton)
        resultTextView = view.findViewById(R.id.resultTextView)
    }

    private fun setupClickListener() {
        convertButton.setOnClickListener {
            handleConversion()
        }
    }

    private fun setupResultTextViewClickListener() {
        resultTextView.setOnClickListener {
            val convertedAmount = resultTextView.text.toString()
            copyToClipboard(convertedAmount)
            showToast("Результат скопирован в буфер обмена")
        }
    }

    private fun handleConversion() {

        val amountStr = amountEditText.text.toString()
        val rateStr = exchangeRateEditText.text.toString()

        // error handling
        if (amountStr.isEmpty() || rateStr.isEmpty()) {
            showToast("Заполните сумму и курс валюты")
            return
        }

        // String to Double
        val amount = amountStr.toDouble()
        val rate = rateStr.toDouble()
        val convertedAmount = convert(amount, rate) //calculate amount

        displayResult(convertedAmount)
    }

    private fun convert(amount: Double, rate: Double): Double {
        return amount * rate //calculate amount
    }

    private fun displayResult(convertedAmount: Double) {
        // Amount TextView
        val formattedConvertedAmount = decimalFormat.format(convertedAmount)
        resultTextView.text = formattedConvertedAmount
    }

    //ClipBoard
    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("converted_amount", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}