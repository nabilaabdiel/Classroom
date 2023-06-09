package com.abdiel.classroom.ui.update.password

import android.os.Bundle
import androidx.core.view.isEmpty
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.databinding.ActivityUpdatePasswordBinding
import com.abdiel.classroom.ui.home.HomeActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdatePasswordActivity :
    BaseActivity<ActivityUpdatePasswordBinding, UpdatePasswordViewModel>(R.layout.activity_update_password) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (binding.etNewPassword.isEmptyRequired(R.string.label_must_fill) ||
                binding.etOldPassword.isEmptyRequired(R.string.label_must_fill) ||
                binding.etConfirmPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }

            val newPassword = binding.etNewPassword.textOf()
            val oldPassword = binding.etOldPassword.textOf()

            viewModel.changePassword(oldPassword, newPassword)
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
}