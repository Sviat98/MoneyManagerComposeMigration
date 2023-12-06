package com.example.moneymanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class OtherFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_other, container, false)

        val otherButton = view.findViewById<Button>(R.id.otherButton)
        otherButton.setOnClickListener {

            Toast.makeText(context, "В реализации", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}