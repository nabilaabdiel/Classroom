package com.abdiel.classroom.ui.update.profile

import androidx.lifecycle.viewModelScope
import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.school.School
import com.abdiel.classroom.data.session.Session
import com.abdiel.classroom.data.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel  @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {

    private val _school = MutableSharedFlow<List<School>>()
    val schools = _school.asSharedFlow()

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

    fun updateProfile (name: String, school_id: Int) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.editProfile(name, school_id)}, false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    session.saveUser(data)
                    _apiResponse.emit(ApiResponse().responseSuccess("profile updated"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

    fun updateProfileImg(name: String, school_id: Int, photo: File) = viewModelScope.launch {
        val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)

        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.editProfileImg(name, school_id, filePart) }, false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    session.saveUser(data)
                    _apiResponse.emit(ApiResponse().responseSuccess("profile updated"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }
}