package com.abdiel.classroom.ui.login

import androidx.lifecycle.viewModelScope
import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.session.Session
import com.abdiel.classroom.data.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: Session,
    private val gson: Gson
): BaseViewModel() {

    fun login(phoneNumber: String, password: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        val deviceToken = session.getString(Const.TOKEN.DEVICE_TOKEN)
        ApiObserver(
            { apiService.login( deviceToken, phoneNumber, password) }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    session.saveUser(data)

                    session.setValue(Const.USER.PHONE_NUMBER, phoneNumber)
                    session.setValue(Const.USER.PASSWORD, password)
                    session.setValue(Const.USER.PROFILE, "Login")
                    session.setValue(Const.TOKEN.PREF_TOKEN, response.getString("token"))

                    _apiResponse.emit(ApiResponse().responseSuccess("Logging in"))
                }
            })
    }
}