package com.yujin.onionmarket.view

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yujin.onionmarket.R

class MainActivity : AppCompatActivity() {
    private var isLocationOpen = false
    private lateinit var arrowAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<HomeFragment>(R.id.container)
            }
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    moveHome()
                    true
                }
                R.id.city_life -> {
                    moveCityLife()
                    true
                }
                R.id.near_me -> {
                    moveNearMe()
                    true
                }
                R.id.chat -> {
                    moveChat()
                    true
                }
                R.id.account -> {
                    moveAccount()
                    true
                }
                else -> false
            }
        }
    }

    //홈
    private fun moveHome() {
        supportFragmentManager.commit {
            replace<HomeFragment>(R.id.container)
        }
    }
    
    //동네생활
    private fun moveCityLife() {
        supportFragmentManager.commit {
            replace<CityLifeFragment>(R.id.container)
        }
    }
    
    //내 근처
    private fun moveNearMe() {
        supportFragmentManager.commit {
            replace<NearMeFragment>(R.id.container)
        }
    }
    
    //채팅
    private fun moveChat() {
        supportFragmentManager.commit {
            replace<ChatFragment>(R.id.container)
        }
    }
    
    //나의 양파
    private fun moveAccount() {
        supportFragmentManager.commit {
            replace<AccountFragment>(R.id.container)
        }
    }

    // Fragment 이동
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.container, fragment)
        }
    }
}