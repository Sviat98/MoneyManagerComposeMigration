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

class ConverterFragment : Fragment() {

    private lateinit var amountEditText: EditText
    private lateinit var exchangeRateEditText: EditText
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView

    private val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_converter, container, false)
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
        }
    }

    private fun handleConversion() {
        val amountStr = amountEditText.text.toString()
        val rateStr = exchangeRateEditText.text.toString()

        if (amountStr.isEmpty() || rateStr.isEmpty()) {
            showToast("Заполните сумму и курс валюты")
            return
        }

        val amount = amountStr.toDouble()
        val rate = rateStr.toDouble()
        val convertedAmount = convert(amount, rate)

        displayResult(convertedAmount)
    }

    private fun convert(amount: Double, rate: Double): Double {
        return amount * rate
    }

    private fun displayResult(convertedAmount: Double) {
        val formattedConvertedAmount = decimalFormat.format(convertedAmount)

        resultTextView.text = formattedConvertedAmount
    }

    private fun copyToClipboard(text: String) {
        if (text.isEmpty()){
            showToast("Заполните сумму и курс валюты")
            return
        }
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("converted_amount", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
