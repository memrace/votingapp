package com.northis.votingapp.di.component

import com.northis.votingapp.authorization.AuthorizationActivity
import com.northis.votingapp.di.module.AuthenticationModule
import com.northis.votingapp.di.module.CommonModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CommonModule::class, AuthenticationModule::class])
interface AuthenticationComponent {
  fun inject(authorizationActivity: AuthorizationActivity)
}