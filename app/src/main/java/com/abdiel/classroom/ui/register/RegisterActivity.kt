package com.abdiel.classroom.ui.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.school.School
import com.abdiel.classroom.databinding.ActivityRegisterBinding
import com.abdiel.classroom.ui.home.HomeActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    private var schoolId = 0
    private val schools = ArrayList<School?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRegister.setOnClickListener {
            if (binding.etName.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPhoneNumber.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPassword.isEmptyRequired(R.string.label_must_fill) ||
                binding.etConfirmPassword.isEmptyRequired(R.string.label_must_fill) ||
                binding.etSchool.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }

            val name = binding.etName.textOf()
            val phone = binding.etPhoneNumber.textOf()
            val password = binding.etPassword.textOf()
            val confirmPassword = binding.etConfirmPassword.textOf()

            if (password.length < 8) {
                binding.root.snacked("Password minimum 8 characters")
//                binding.etPassword.error = "Password minimum 8 characters"

                return@setOnClickListener
            }

            if (password.length > 20) {
                binding.root.snacked("Password maximum 20 characters")
//                binding.etPassword.requestFocus()
//                binding.etPassword.error = "Password maximum 15 characters"

                return@setOnClickListener
            }

            viewModel.register(name, phone, schoolId, password, confirmPassword)
        }

//        val languages = resources.getStringArray(R.array.school)
//        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, languages)
//        binding.etSchool.setAdapter(arrayAdapter)

        binding.etSchool.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = schools[p2]
                schoolId = item?.id ?: 0
            }
        }

        binding.btnLogin.setOnClickListener{
            finish()
        }

        viewModel.getSchool()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Logging in...")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }

                launch {
                    viewModel.schools.collect {
                        val arrayAdapter = ArrayAdapter(this@RegisterActivity, R.layout.dropdown_item, it)
                        binding.etSchool.setAdapter(arrayAdapter)
                        schools.clear()
                        schools.addAll(it)
                    }
                }
            }
        }
    }
}