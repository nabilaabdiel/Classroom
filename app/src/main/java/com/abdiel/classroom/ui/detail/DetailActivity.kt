package com.abdiel.classroom.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.user.User
import com.abdiel.classroom.databinding.ActivityDetailBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.helper.ImagePreviewHelper
import kotlinx.coroutines.launch

class DetailActivity : BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {

    private var friends: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()

        binding.btnBack.setOnClickListener {
            finish()
        }

        //imgPreview
//        binding.circleProfile.setOnClickListener {
//            ImagePreviewHelper(this).show(binding.circleProfile, friends?.photo)
//        }

        friends = intent.getParcelableExtra(Const.FRIENDS.FRIENDS)
        binding.detail = friends

        binding.etPhoneNumber.setOnClickListener {
            whatsapp(friends?.phone)
        }
    }

    //whatsapp function
    private fun whatsapp(number: String?) {
        val intentUri = Uri.parse("https://api.whatsapp.com/send?phone="+number)
        val whatsappIntent = Intent(Intent.ACTION_VIEW)
        whatsappIntent.setData(intentUri)
        startActivity(whatsappIntent)
    }

    private fun observe() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        if (it.status == ApiStatus.SUCCESS) {
                        }
                    }
                }
            }
        }
    }
}