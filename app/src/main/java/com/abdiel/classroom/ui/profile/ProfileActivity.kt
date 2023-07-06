package com.abdiel.classroom.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.user.User
import com.abdiel.classroom.databinding.ActivityProfileBinding
import com.abdiel.classroom.ui.home.HomeActivity
import com.abdiel.classroom.ui.login.LoginActivity
import com.abdiel.classroom.ui.update.password.UpdatePasswordActivity
import com.abdiel.classroom.ui.update.profile.UpdateProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.helper.ImagePreviewHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getProfile() {success ->
            if (success) {
                getUser()
            } else {
                binding.root.snacked(R.string.failed_data_profile)
            }
        }

        binding.image.setOnClickListener {
            ImagePreviewHelper(this).show(binding.image, binding.user?.photo)
        }

        //btn back
        binding.btnBack.setOnClickListener {
            finish()
        }

        //btn logout
        binding.btnLogout.setOnClickListener {
            alertDialog()
        }

        //btn edit profile
        binding.btnEditProfile.setOnClickListener {
            activityLauncher.launch(createIntent<UpdateProfileActivity>()) {
                getUser()
            }
        }

        //btn edit password
        binding.btnEditPassword.setOnClickListener {
            openActivity<UpdatePasswordActivity>()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect { logout ->
                        if (logout.status == ApiStatus.SUCCESS) {
                            openActivity<LoginActivity>()
                            finishAffinity()
                        }
                    }
                }
            }
        }
    }

    //data user
    private fun getUser() {
        val users = session.getUser()
        binding.user = users
    }

    //for alert dialog
    private fun alertDialog() {
        val alertDialog = LayoutInflater.from(this)
            .inflate(R.layout.alert_dialog, findViewById(R.id.alert_dialog))

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(alertDialog)

        val dialog = alertDialogBuilder.show()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val btnCancel = alertDialog.findViewById<AppCompatButton>(R.id.btn_cancel)
        val btnLogout = alertDialog.findViewById<AppCompatButton>(R.id.btn_logout)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnLogout.setOnClickListener {
            viewModel.logout()
            dialog.dismiss()
        }
    }
}