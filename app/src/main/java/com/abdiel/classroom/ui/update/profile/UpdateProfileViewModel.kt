package com.abdiel.classroom.ui.update.profile

import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.session.Session
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel  @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {
}