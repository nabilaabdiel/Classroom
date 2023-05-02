package com.abdiel.classroom.ui.register

import androidx.lifecycle.viewModelScope
import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.school.School
import com.abdiel.classroom.data.session.Session
import com.abdiel.classroom.data.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

    ): BaseViewModel() {

    private val _school = MutableSharedFlow<List<School>>()
    val schools = _school.asSharedFlow()

    fun register(name: String, phone: String, schoolId: Int, password: String, confirmPassword: String)
    = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            {apiService.register(name, phone, schoolId, password, confirmPassword)},
            false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                _apiResponse.emit(ApiResponse().responseSuccess())
            }
        })
    }

    fun getSchool() = viewModelScope.launch {
        ApiObserver(
            {apiService.getSchool()},
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                        val data = response.getJSONArray(ApiCode.DATA).toList<School>(gson)
                        _school.emit(data)
                }
            })
    }
}