package com.example.moneymanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class ConverterFragment : Fragment() {

    private lateinit var amountEditText: EditText
    private lateinit var fromCurrencyEditText: EditText
    private lateinit var toCurrencyEditText: EditText
    private lateinit var convertButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_converter, container, false)

        amountEditText = view.findViewById(R.id.amountEditText)
        fromCurrencyEditText = view.findViewById(R.id.fromCurrencyEditText)
        toCurrencyEditText = view.findViewById(R.id.toCurrencyEditText)
        convertButton = view.findViewById(R.id.convertButton)

        convertButton.setOnClickListener {
            convertCurrency()
        }

        return view
    }

    private fun convertCurrency() {
        val amountStr = amountEditText.text.toString()
        val fromCurrencyStr = fromCurrencyEditText.text.toString()
        val toCurrencyStr = toCurrencyEditText.text.toString()

        if (amountStr.isEmpty() || fromCurrencyStr.isEmpty() || toCurrencyStr.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Заполните все поля",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val amount = amountStr.toDouble()
        val fromCurrency = fromCurrencyStr.toDouble()
        val toCurrency = toCurrencyStr.toDouble()

        val result = convert(amount, fromCurrency, toCurrency)
        Toast.makeText(
            requireContext(),
            "$amount = $result",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun convert(amount: Double, fromCurrency: Double, toCurrency: Double): Double {
        return amount * (toCurrency / fromCurrency)
    }
}
