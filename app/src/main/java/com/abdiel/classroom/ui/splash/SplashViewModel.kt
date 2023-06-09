package com.abdiel.classroom.ui.splash

import androidx.lifecycle.viewModelScope
import com.abdiel.classroom.base.viewModel.BaseViewModel
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.session.Session
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val session: Session): BaseViewModel() {
    fun splash(done: (Boolean) -> Unit) = viewModelScope.launch {

        val userLogin = session.getString(Const.USER.PROFILE)

        done (userLogin == "Login")
    }
}