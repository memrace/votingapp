package com.northis.votingapp.di.component

import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.di.module.ApiModule
import com.northis.votingapp.di.module.AuthenticationModule
import com.northis.votingapp.di.module.CommonModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CommonModule::class, ApiModule::class, AuthenticationModule::class])
interface ActivityComponent {
  fun inject(appActivity: AppActivity)
}