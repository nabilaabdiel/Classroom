package com.abdiel.classroom.ui.profile

import androidx.lifecycle.viewModelScope
import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.school.School
import com.abdiel.classroom.data.session.Session
import com.abdiel.classroom.data.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
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
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: Session,
    private val gson: Gson
    ) : BaseViewModel() {

    private val _school = MutableSharedFlow<School>()
    val schools = _school.asSharedFlow()

    fun logout() = viewModelScope.launch {
        ApiObserver(
            { apiService.logout() }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    //delete account
                    session.clearUser()

                    session.setValue(Const.USER.PHONE_NUMBER, "")
                    session.setValue(Const.USER.PASSWORD, "")
                    session.setValue(Const.USER.PROFILE, "")
                    session.setValue(Const.TOKEN.PREF_TOKEN, "")
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            })
    }

    fun getProfile(result: (Boolean)-> Unit) = viewModelScope.launch {
        ApiObserver(
            { apiService.getProfile() }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject("data").toObject<User>(gson)
                    session.saveUser(data)
                    result(true)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    result(false)
                }
            })
    }

    fun getSchool(schoolId: Int) = viewModelScope.launch {
        ApiObserver(
            {apiService.getSchool()},
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<School>(gson)
                    _school.emit(data.firstOrNull { it.id == schoolId } ?: return)
                }
            })
    }
}