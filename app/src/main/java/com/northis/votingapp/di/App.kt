package com.northis.votingapp.di

import android.app.Application
import com.northis.votingapp.di.component.ActivityComponent
import com.northis.votingapp.di.component.AuthenticationComponent
import com.northis.votingapp.di.component.DaggerActivityComponent
import com.northis.votingapp.di.component.DaggerAuthenticationComponent
import com.northis.votingapp.di.module.ApiModule
import com.northis.votingapp.di.module.AuthenticationModule
import com.northis.votingapp.di.module.CommonModule

class App : Application() {
  lateinit var authenticationComponent: AuthenticationComponent
  lateinit var activityComponent: ActivityComponent
  override fun onCreate() {
    super.onCreate()
    val commonModule = CommonModule(this)
    val authenticationModule = AuthenticationModule()
    val apiModule = ApiModule("https://192.168.100.8:5002/")
    authenticationComponent = DaggerAuthenticationComponent.builder()
      .commonModule(commonModule)
      .authenticationModule(authenticationModule)
      .build()
    activityComponent =
       DaggerActivityComponent.builder().commonModule(commonModule).apiModule(apiModule)
	.authenticationModule(authenticationModule).build()
  }

}