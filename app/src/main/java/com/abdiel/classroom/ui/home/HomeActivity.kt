package com.abdiel.classroom.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.ui.detail.DetailActivity
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.user.User
import com.abdiel.classroom.databinding.ActivityHomeBinding
import com.abdiel.classroom.databinding.ListFriendBinding
import com.abdiel.classroom.ui.profile.ProfileActivity
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    //listFriend
    private var friendList = ArrayList<User?>()

    private val adapter by lazy {
        ReactiveListAdapter<ListFriendBinding, User>(R.layout.list_friend)
            .initItem { position, data ->
                openActivity<DetailActivity> {
                    putExtra(Const.LIST.LIST_FRIEND, data)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.photoProfile.setOnClickListener {
//            activityLauncher.launch(createIntent<ProfileActivity>()) {
//                getUser()
//            }
//    }
        binding.photoProfile.setOnClickListener {
            openActivity<ProfileActivity> ()
        }

            binding.rvListFriend.adapter = adapter
            initSwipe()
            getList()
            getUser()
            observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.listFriendHome.collect {Listfriend ->
                        Log.d("cek", "data: $Listfriend")
                        friendList.clear()
                        adapter.submitList(friendList)
                        friendList.addAll(Listfriend)
                        binding.rvListFriend.adapter?.notifyItemInserted(0)
                        binding.swipeRefresh.isRefreshing = false

                        if (Listfriend.isEmpty()) {
                            binding.tvBlankData.visibility = View.VISIBLE
                        } else {
                            binding.tvBlankData.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun getList() {
        viewModel.getFriend()
    }

    private fun initSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getFriend()
        }
    }

    //data user
    private fun getUser() {
        val users = session.getUser()
        binding.user = users
    }
}