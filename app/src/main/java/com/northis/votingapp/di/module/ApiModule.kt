package com.northis.votingapp.di.module

import android.app.Application
import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.northis.votingapp.app.CommonModel
import com.northis.votingapp.authorization.IUserManager
import com.northis.votingapp.authorization.UnsafeConnection
import com.northis.votingapp.voting.VotingModel
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.net.ssl.X509TrustManager

@Module
class ApiModule(private val baseUrl: String) {
  @Provides
  fun provideHttpCache(application: Application): Cache {
    val cacheSize = 10 * 1024 * 1024
    return Cache(application.cacheDir, cacheSize.toLong())
  }

  @Provides
  @Singleton
  fun provideGson(): Gson {
    val gsonBuilder = GsonBuilder()
    with(gsonBuilder) {
      setDateFormat("yyyy-MM-dd")
      setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    }

    return gsonBuilder.create()
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(
    context: Context,
    cache: Cache,
    userManager: IUserManager
  ): OkHttpClient {
    val client = OkHttpClient.Builder()
    with(client) {
      sslSocketFactory(
        UnsafeConnection.getSslSocketFactory(),
        UnsafeConnection.getTrustAllCerts()[0] as X509TrustManager
      )
      hostnameVerifier { _, _ -> true }
      cache(cache)
      addInterceptor(Interceptor { chain ->
        val request = chain.request()
        val newRequest = request
          .newBuilder()
          .addHeader(
            "Authorization",
            "Bearer ${userManager.getAccessToken(context)}"
          )
          .addHeader("Content-Type", "application/json")
          .build()
        return@Interceptor chain.proceed(newRequest)
      })
    }
    return client.build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }

  @Provides
  @Singleton
  fun provideUserApi(retrofit: Retrofit): CommonModel.IProfileApi {
    return retrofit.create(CommonModel.IProfileApi::class.java)
  }

  @Provides
  @Singleton
  fun provideVotingApi(retrofit: Retrofit): VotingModel.IVotingApi {
    return retrofit.create(VotingModel.IVotingApi::class.java)
  }

//  @Provides
//  @Singleton
//  fun provideCatalogApi(retrofit: Retrofit):  {
//    return retrofit.create(ICatalogService::class.java)
//  }

}