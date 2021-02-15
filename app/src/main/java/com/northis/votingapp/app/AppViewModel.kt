package com.northis.votingapp.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.northis.votingapp.authorization.AuthorizationModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AppViewModel(private val commonModel: CommonModel, private val authorizationModel: AuthorizationModel) : ViewModel() {
  val logout by lazy { authorizationModel.authorizationService.signOut() }

  fun loadUser() = liveData(Dispatchers.IO) {
    emit(commonModel.loadUser())
  }
}

class AppViewModelFactory @Inject constructor(
  application: Application,
  private val commonModel: CommonModel,
  private val authorizationModel: AuthorizationModel
) : ViewModelProvider.AndroidViewModelFactory(application) {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return AppViewModel(commonModel, authorizationModel) as T
  }
}