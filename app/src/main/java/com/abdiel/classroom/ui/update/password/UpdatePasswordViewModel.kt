package com.abdiel.classroom.ui.update.password

import androidx.lifecycle.viewModelScope
import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.session.Session
import com.abdiel.classroom.data.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {

    fun changePassword(oldPassword: String, newPassword: String)
    = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.changePassword(oldPassword, newPassword) }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val message = response.getString(ApiCode.MESSAGE)
//                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
//                    session.saveUser(data)

                    _apiResponse.emit(ApiResponse().responseSuccess(message))
                }
            })
    }

}