package com.abdiel.classroom.ui.update.password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.databinding.ActivityUpdatePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordActivity : BaseActivity<ActivityUpdatePasswordBinding, UpdatePasswordViewModel>(R.layout.activity_update_password) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}