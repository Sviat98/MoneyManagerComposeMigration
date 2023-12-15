package com.example.moneymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TransactionAdapter(private val transactions: List<String>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {//Связь с RecyclerView.

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //ViewHolder для хранения списка.
        // Элемент TextView для отображения деталей транзакции.
        val transactionDetailsTextView: TextView = itemView.findViewById(R.id.transactionDetailsTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Получение данных о транзакции из списка по позиции.
        val transaction = transactions[position]
        // SetTextView отображения транзакции.
        holder.transactionDetailsTextView.text = transaction
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}
