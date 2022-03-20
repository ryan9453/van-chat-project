package com.ryan.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ryan.chat.databinding.FragmentHomeBinding
import com.ryan.chat.databinding.RowChatroomBinding
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    companion object {
        val TAG = HomeFragment::class.java.simpleName
        val instance : HomeFragment by lazy {
            HomeFragment()
        }
    }
    lateinit var binding: FragmentHomeBinding
    val roomViewModel by viewModels<RoomViewModel>()
    var adapter = ChatRoomAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
//        return super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 由此處開始寫 code
        val parentActivity = requireActivity() as MainActivity
        val prefLogin = requireContext().getSharedPreferences("login", AppCompatActivity.MODE_PRIVATE)
        val login = prefLogin.getBoolean("login_state", false)
        val username = prefLogin.getString("login_userid", "")

        if (login) {
            parentActivity.binding.tvHomeLoginUserid.setText(username)
            parentActivity.binding.imHead.visibility = View.VISIBLE
        }
        else parentActivity.binding.tvHomeLoginUserid.setText("")

        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = GridLayoutManager(requireContext(),2)
        binding.recycler.adapter = adapter

        // viewModel
        // 觀察 RoomViewModel裡的 chatRooms(liveData)
        // 如果 chatRooms
        roomViewModel.chatRooms.observe(viewLifecycleOwner) { rooms ->
            adapter.submitRooms(rooms)
        }
        roomViewModel.getAllRooms()
        Log.d(TAG, "跑過 getAllRooms")
//        roomViewModel.getHitRooms()
//        Log.d(TAG, "跑過 getHitRooms")


    }
    inner class ChatRoomAdapter : RecyclerView.Adapter<BindingViewHolder>() {
        val chatRooms = mutableListOf<Lightyear>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
            val binding = RowChatroomBinding.inflate(layoutInflater, parent , false)
            return BindingViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
            val lightYear = chatRooms[position]
            holder.streaName.setText(lightYear.nickname)
            holder.title.setText(lightYear.stream_title)
            holder.tags.setText(lightYear.tags)
            Glide.with(this@HomeFragment).load(lightYear.head_photo)
                .into(holder.headPhoto)
            holder.itemView.setOnClickListener {
                chatRoomClicked(lightYear)
            }
        }

        override fun getItemCount(): Int {
            return chatRooms.size
        }
        fun submitRooms(rooms: List<Lightyear>) {
            chatRooms.clear()
            chatRooms.addAll(rooms)
            Log.d(TAG, "rooms of num = ${rooms.size}")
            Log.d(TAG, "第一間房間是 = ${chatRooms[0].nickname}")
            notifyDataSetChanged()
            Log.d(TAG, "第一間房間是 = ${chatRooms[0].nickname}")
        }

    }
    inner class BindingViewHolder(val binding: RowChatroomBinding):
        RecyclerView.ViewHolder(binding.root) {
        val streaName = binding.tvStreamName
        val title = binding.tvTitle
        val headPhoto = binding.imHeadPhoto
        val tags = binding.tvTags
    }

    fun chatRoomClicked(lightyear: Lightyear) {
        val parentActivity =  requireActivity() as MainActivity
        parentActivity.supportFragmentManager.beginTransaction().run {
            replace(R.id.main_container, parentActivity.mainFragments[0])
            replace(R.id.chat_container, parentActivity.chatFragments[1])

            commit()
        }
        parentActivity.binding.bottonNavBar.visibility = View.GONE
        parentActivity.binding.searchContainer.visibility = View.GONE
        parentActivity.binding.imHead.visibility = View.GONE
        parentActivity.binding.tvHomeLoginUserid.visibility = View.GONE
    }

}