package com.example.moneymanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val settingButton = view.findViewById<Button>(R.id.settingButton)
        settingButton.setOnClickListener {

            Toast.makeText(context, "В реализации", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}