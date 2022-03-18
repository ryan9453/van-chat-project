package com.ryan.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ryan.chat.databinding.FragmentPersonBinding

class PersonFragment : Fragment() {
    companion object {
        val TAG = PersonFragment::class.java.simpleName
    }
    lateinit var binding: FragmentPersonBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonBinding.inflate(inflater)
//        return super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefLogin = requireContext().getSharedPreferences("login", AppCompatActivity.MODE_PRIVATE)
        var login_userid = prefLogin.getString("login_userid", "")

        // 顯示用戶暱稱和帳號用
        val prefUser = requireContext().getSharedPreferences("userinfo", AppCompatActivity.MODE_PRIVATE)
        // 用取得的帳號去 userinfo資料夾索引取得暱稱
        var username = prefUser.getString("${login_userid}name", "")
        binding.tvPersonShowUserid.setText(login_userid)
        binding.tvPersonShowName.setText(username)

        binding.btLogout.setOnClickListener {
            val parentActivity =  requireActivity() as MainActivity
            var login = prefLogin.getBoolean("login_state", false)
            login = false
            prefLogin.edit()
                .putBoolean("login_state", login)
                .putString("login_userid", "")
                .apply()
            Log.d(TAG, "Login_state = $login")
            parentActivity.supportFragmentManager.beginTransaction().run {
                // mainFragments[3] = LoginFragment
                replace(R.id.main_container, parentActivity.mainFragments[3])
                commit()
            }
        }
    }
}