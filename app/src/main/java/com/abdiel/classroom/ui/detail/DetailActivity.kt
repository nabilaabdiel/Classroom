package com.abdiel.classroom.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.user.User
import com.abdiel.classroom.databinding.ActivityDetailBinding
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.ImagePreviewHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {

    private var friendsPhone : String? = null
    private var userId : Int? = null
    private var userName : String? = null
    private var userFriend : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getUser()

        binding.btnBack.setOnClickListener {
            finish()
        }

        val detail = intent.getParcelableExtra<User>(Const.LIST.LIST_FRIEND)
        binding.detail = detail
        binding.detail = detail
        friendsPhone = detail?.phone
        userId = detail?.id
        userName = detail?.name

        binding.circleProfile.setOnClickListener {
            ImagePreviewHelper(this).show(binding.circleProfile, binding.detail?.photo)
        }

        binding.etPhoneNumber.setOnClickListener {
            whatsapp()
        }

        binding.btnNotification.setOnClickListener {
            viewModel.sendNotification("Classroom",  "$userFriend Greets you $userName", userId)
        }
        // https://magang.crocodic.net/ki/Afifun/kelasku/public/api/auth/sendNotification
        // https://magang.crocodic.net/ki/Afifun/kelasku/public/api/auth/sendNotification
    }

    //data user
    private fun getUser() {
        val users = session.getUser()
        userFriend = users?.name
    }

    //whatsapp function
    private fun whatsapp(number: String = "+62 +$friendsPhone") {
        val intentUri = Uri.parse("https://api.whatsapp.com/send?phone="+number)
        val whatsappIntent = Intent(Intent.ACTION_VIEW)
        whatsappIntent.setData(intentUri)
        startActivity(whatsappIntent)
    }}