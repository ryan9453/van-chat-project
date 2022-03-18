package com.ryan.chat

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.ryan.chat.databinding.ActivityMainBinding
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    lateinit var binding: ActivityMainBinding
    val mainFragments = mutableListOf<Fragment>()
    val chatFragments = mutableListOf<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchContainer.visibility = View.GONE

        initFragments()



        binding.bottonNavBar.setOnItemSelectedListener { item ->
            val prefLogin = getSharedPreferences("login", AppCompatActivity.MODE_PRIVATE)
            var login = prefLogin.getBoolean("login_state", false)
            when (item.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction().run {
                        replace(R.id.main_container, mainFragments[1])
                        commit()
                    }
                    binding.searchContainer.visibility = View.GONE
                    true
                }
                R.id.action_person -> {
                    Log.d(TAG, "login_state = $login")
                    if (login) {
                        Log.d(TAG, "有登入去個人資訊")
                        supportFragmentManager.beginTransaction().run {
                        replace(R.id.main_container, mainFragments[2])
                        commit() }
                    } else {
                        Log.d(TAG, "未登入去登入頁面")
                        supportFragmentManager.beginTransaction().run {
                            replace(R.id.main_container, mainFragments[3])
                            commit() }
                    }
                    binding.searchContainer.visibility = View.GONE
                    true
                }
                R.id.action_search -> {
                    supportFragmentManager.beginTransaction().run {
                        replace(R.id.main_container, mainFragments[1])
                        commit()
                    }
                    binding.searchContainer.visibility = View.VISIBLE
                    true
                }
                else -> true
            }
        }
    }

    private fun initFragments() {
        mainFragments.add(0, EmptyFragment())
        mainFragments.add(1, HomeFragment())
        mainFragments.add(2, PersonFragment())
        mainFragments.add(3, LoginFragment())
        mainFragments.add(4, SignUpFragment())
        mainFragments.add(5, PhotoFragment())

        chatFragments.add(0, EmptyFragment())
        chatFragments.add(1, RoomFragment())
        supportFragmentManager.beginTransaction().run {
            add(R.id.main_container, mainFragments[1])
            add(R.id.chat_container, chatFragments[0])
            add(R.id.search_container, SearchFragment())
            commit()
        }

    }
}






