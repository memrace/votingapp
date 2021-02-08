package com.northis.votingapp.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CommonModule(private val application: Application) {
  @Singleton
  @Provides
  fun provideApplication(): Application {
    return application
  }

  @Singleton
  @Provides
  fun provideContext(): Context {
    return application.applicationContext
  }
}