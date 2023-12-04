package com.example.moneymanager

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Скрыть ActionBar/Toolbar
        supportActionBar?.hide()

        // Установить цвет статус-бара
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            )
            window.statusBarColor = getColor(R.color.mainBackground)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.mainBackground)
        }

        bottomNavigationView = findViewById(R.id.button_navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment: Fragment = when (menuItem.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_other -> otherFragment()
                R.id.nav_wallet -> WalletFragment()
                R.id.nav_converter -> ConverterFragment()
                R.id.nav_setting -> SettingFragment()
                else -> HomeFragment()
            }

            replaceFragment(selectedFragment)
            true
        }

        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}
