package com.abdiel.classroom.ui.update.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.databinding.ActivityUpdateProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateProfileActivity : BaseActivity<ActivityUpdateProfileBinding, UpdateProfileViewModel> (R.layout.activity_update_profile) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}