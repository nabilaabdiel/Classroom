package com.abdiel.classroom.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.databinding.ActivityProfileBinding
import com.abdiel.classroom.ui.home.HomeActivity
import com.abdiel.classroom.ui.login.LoginActivity
import com.abdiel.classroom.ui.update.password.UpdatePasswordActivity
import com.abdiel.classroom.ui.update.profile.UpdateProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
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
                binding.root.snacked("Gagal ambil data profile")
            }
        }

        //btn back
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        //btn logout
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        //btn edit profile
        binding.btnEditProfile.setOnClickListener {
            //get data
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
                //id-> string school name
                launch {
                    viewModel.schools.collect {
                        binding.school = it.schoolName
                    }
                }
            }
        }
    }

    //data user
    private fun getUser() {
        val users = session.getUser()
        binding.user = users
        //id-> string school name
        viewModel.getSchool(users?.schoolId ?: return)
    }
}