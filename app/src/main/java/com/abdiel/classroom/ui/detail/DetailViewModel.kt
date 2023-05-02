package com.abdiel.classroom.ui.detail

import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.session.Session
import com.google.gson.Gson
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {

}