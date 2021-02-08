package com.northis.votingapp.authorization

import android.app.Application
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class AuthorizationViewModel(private val authorizationModel: AuthorizationModel) : ViewModel() {

  suspend fun checkAuthority(webView: WebView, callback: IAuthorizationEventHandler) {
    authorizationModel.authorizationService.checkAuthorization(webView, callback)
  }

  class AuthorizationViewModelFactory @Inject constructor(
    application: Application,
    private val authorizationModel: AuthorizationModel
  ) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return AuthorizationViewModel(authorizationModel) as T
    }
  }
}