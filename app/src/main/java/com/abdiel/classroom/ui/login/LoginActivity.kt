package com.abdiel.classroom.ui.login

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.databinding.ActivityLoginBinding
import com.abdiel.classroom.ui.home.HomeActivity
import com.abdiel.classroom.ui.register.RegisterActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        generateFcmToken()

        binding.btnRegister.setOnClickListener {
            openActivity<RegisterActivity>()
        }

        binding.btnLogin.setOnClickListener {
            if (binding.etPhoneNumber.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }

            val email = binding.etPhoneNumber.textOf()
            val password = binding.etPassword.textOf()

            generateFcmToken {
                viewModel.login(email, password)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Logging in ..")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finishAffinity()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }

    private fun generateFcmToken ( result:( Boolean )-> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                result(false)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            //todo: menerima hasil tugas fcmnya
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token) //todo:untuk menegecek aja
            Log.d(ContentValues.TAG, msg)
            session.setValue(Const.TOKEN.DEVICE_TOKEN, token)
            result(true)
        })
    }
}