package com.northis.votingapp.di.module

import android.app.Application
import android.content.Context
import coil.ImageLoader
import com.northis.votingapp.authorization.UnsafeConnection
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton
import javax.net.ssl.X509TrustManager

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