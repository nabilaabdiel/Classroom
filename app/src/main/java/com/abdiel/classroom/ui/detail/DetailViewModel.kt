package com.abdiel.classroom.ui.detail

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
class DetailViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {

    fun sendNotification(title: String?, body: String?, userId: Int?) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.sendNotification(title, body, userId) }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {

//                    _apiResponse.emit(ApiResponse().responseSuccess("Logging in"))
                }
            })
    }

}