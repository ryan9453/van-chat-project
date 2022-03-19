package com.ryan.chat

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ryan.chat.databinding.FragmentRoomBinding
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class RoomFragment : Fragment() {
        companion object {
            val TAG = RoomFragment::class.java.simpleName
            val instance : RoomFragment by lazy {
                RoomFragment()
            }
        }
        lateinit var binding: FragmentRoomBinding
        lateinit var websocket: WebSocket

        // 測試用 直播室網址（圖片版
        val mapOfRoomId = mapOf(
            "5015" to "https://i.imgur.com/bou4Cag.jpg", // 水水
            "5013" to "https://i.imgur.com/WbSlIAX.jpg", // 可比
            "5019" to "https://i.imgur.com/SdsqyXM.jpg", // 乐乐
            "5018" to "https://i.imgur.com/fPeogox.jpg", // 跳跳
            "5011" to "https://i.imgur.com/DUFDOxV.jpg", // Bee
            "5007" to "https://i.imgur.com/P5HmYNP.jpg", // 凌晨🌛
            "5016" to "https://i.imgur.com/dBnoHFo.jpg", // 妍淨
            "5014" to "https://i.imgur.com/NMG1Bf3.jpg", // 佳佳
            "5010" to "https://i.imgur.com/sb2J0TF.jpg", // 燕子
            "5012" to "https://i.imgur.com/VqtHiV6.jpg", // 肉肉
            "4971" to "https://i.imgur.com/viHyLC0.jpg", // 直播小帮手
            "5020" to "https://i.imgur.com/0QucvHy.jpg", // 小檸檬
            "5003" to "https://i.imgur.com/eI8KK9I.jpg", // 暖暖
            "5008" to "https://i.imgur.com/D1r3OYl.jpg", // 安苡萱
            "4972" to "https://i.imgur.com/BLUSgdg.jpg", // 直播小帮手
            "5017" to "https://i.imgur.com/jRv6i92.jpg", // 錢錢
        )

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentRoomBinding.inflate(inflater)
//        return super.onCreateView(inflater, container, savedInstanceState)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            // 由此處開始寫 code
            val parentActivity =  requireActivity() as MainActivity
            val prefLogin = requireContext().getSharedPreferences("login", AppCompatActivity.MODE_PRIVATE)
            val prefUser = requireContext().getSharedPreferences("userinfo", AppCompatActivity.MODE_PRIVATE)
            var login = prefLogin.getBoolean("login_state", false)
            var user = prefLogin.getString("login_userid", "")
            var username = prefUser.getString("${user}name", "guest")
            var requestName = "guest"

            if (login) {
                requestName = username.toString()
            }

            var path = "girl"
            var vidPath = "android.resource://"+requireContext().packageName+"/raw/$path"
            var uri = Uri.parse(vidPath)

            val client = OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder()
                .url("wss://lott-dev.lottcube.asia/ws/chat/chat:app_test?nickname=$requestName")
                .build()

            websocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    Log.d(TAG, ": onClosed");
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    Log.d(TAG, ": onClosing");
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    Log.d(TAG, ": onFailure");
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    val json = text
                    if ("sys_updateRoomStatus" in json) {
                        val response = Gson().fromJson(json, UpdateRoomStatus::class.java)
                        var action = response.body.entry_notice.action
                        if (action == "enter") {
                            Log.d(TAG, "歡迎 ${response.body.entry_notice.username} 進到直播間")
                        } else if (action == "leave") {
                            Log.d(TAG, " ${response.body.entry_notice.username} 已離開直播間")
                        }
                    } else if ("admin_all_broadcast" in json) {
                        val response = Gson().fromJson(json, AllBroadcast::class.java)
                        Log.d(TAG, "${response.body.content.en}")
                    }

                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    Log.d(TAG, ": onMessage ${bytes.hex()}");
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    Log.d(TAG, ": onOpen: response = $response")
                }
            })

            binding.vGirl.setVideoURI(uri)
            binding.vGirl.setOnPreparedListener {
                binding.vGirl.start()
            }

            binding.btLeave.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("message")
                    .setMessage("Are you sure you want to leave?")
                    .setPositiveButton("Yes") { d, w ->

                        parentActivity.supportFragmentManager.beginTransaction().run {
                            replace(R.id.main_container, parentActivity.mainFragments[1])
                            replace(R.id.chat_container, parentActivity.chatFragments[0])
                            commit()
                        }
                        parentActivity.binding.bottonNavBar.visibility = View.VISIBLE
                        parentActivity.binding.imHead.visibility = View.VISIBLE
                        parentActivity.binding.tvHomeLoginUserid.visibility = View.VISIBLE
                    }
                    .setNegativeButton("No", null)
                    .show()

            }

        }

}