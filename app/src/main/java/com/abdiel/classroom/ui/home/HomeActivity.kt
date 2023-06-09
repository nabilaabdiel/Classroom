package com.abdiel.classroom.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.user.User
import com.abdiel.classroom.databinding.ActivityHomeBinding
import com.abdiel.classroom.databinding.ListFriendBinding
import com.abdiel.classroom.ui.detail.DetailActivity
import com.abdiel.classroom.ui.profile.ProfileActivity
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

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

        observe()
        initSwipe()
        getList()
        getUser()

        binding.rvListFriend.adapter = adapter

        binding.photoProfile.setOnClickListener {
            activityLauncher.launch(createIntent<ProfileActivity>()) {
                getUser()
            }
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.listFriendHome.collect { Listfriend ->
                        Log.d("Home", "List: $Listfriend")
                        adapter.submitList(Listfriend)
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
            getList()
        }
    }

    override fun onBackPressed() {
        unsavedAlert()
    }

    //data user
    private fun getUser() {
        val homeUser = session.getUser()
        binding.user = homeUser
        Log.d("home", "user:$homeUser")
    }

    private fun unsavedAlert() {
        val alertDialog = LayoutInflater.from(this)
            .inflate(R.layout.alert_dialog_home, findViewById(R.id.alert_dialog_home))

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(alertDialog)

        val dialog = alertDialogBuilder.show()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val btnCancel = alertDialog.findViewById<AppCompatButton>(R.id.btn_stay)
        val btnLogout = alertDialog.findViewById<AppCompatButton>(R.id.btn_exit)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnLogout.setOnClickListener {
            dialog.dismiss()
            finish()
        }
//        val builder = AlertDialog.Builder(this@HomeActivity)
//        builder.setTitle("You want to exit?")
//        builder.setMessage("Yaqin mau keluar hm?")
//                //opsi iya
//            .setPositiveButton("Dismiss") { _, _ ->
//                this@HomeActivity.finish()
//            }
//                //opsi engga
//            .setNegativeButton("Stay here") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//        val dialog: AlertDialog = builder.create()
//
//        // Set the color of the positive button text
//        dialog.setOnShowListener {
//            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.red))
//            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.text))
//        }
//        dialog.show()
    }
}