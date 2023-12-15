package com.example.moneymanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    //NavBar
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //SetМакет из xml
        setContentView(R.layout.activity_main)

        //Инициализация NavBar
        bottomNavigationView = findViewById(R.id.button_navigation)

        //Cлушател событий NavBar
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment: Fragment = when (menuItem.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_wallet -> WalletFragment()
                R.id.nav_converter -> ConverterFragment()
                R.id.nav_setting -> SettingFragment()
                else -> HomeFragment()
            }
            replaceFragment(selectedFragment)
            true
        }
     //Default
        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) { //фрагмант на выбранный
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}
