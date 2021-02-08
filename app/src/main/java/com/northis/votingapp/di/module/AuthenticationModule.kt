package com.northis.votingapp.di.module

import com.northis.votingapp.authorization.IOAuthSettingsProvider
import com.northis.votingapp.authorization.IUserManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthenticationModule {
  @Provides
  @Singleton
  fun provideUserTokenStorage(): IUserManager {
	return IUserManager.instance
  }

  @Provides
  @Singleton
  fun provideOAuthSettings(): IOAuthSettingsProvider {
	return IOAuthSettingsProvider.instance
  }
}