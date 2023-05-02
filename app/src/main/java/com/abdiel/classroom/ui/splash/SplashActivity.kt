package com.abdiel.classroom.ui.splash

import android.os.Bundle
import android.view.View
import com.abdiel.classroom.ui.home.HomeActivity
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.databinding.ActivitySplashBinding
import com.abdiel.classroom.ui.login.LoginActivity
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(R.layout.activity_splash) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnLogin -> {
                viewModel.splash {
                    if (it) {
                        openActivity<HomeActivity>()
                    } else {
                        openActivity<LoginActivity>()
                    }
                    finish()
                }
            }
        }
        super.onClick(v)
    }
}