package com.northis.votingapp.authorization

import android.content.Context
import android.media.DeniedByServerException
import android.net.Uri
import android.net.http.SslError
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.*
import javax.inject.Inject
import javax.net.ssl.X509TrustManager

class AuthorizationModel @Inject constructor(
  private val context: Context,
  private val userManager: IUserManager,
  private val oauthSettingsProvider: IOAuthSettingsProvider
) {

  val authorizationService: AuthorizationService get() = AuthorizationService()

  inner class AuthorizationService {

    // Создаем и проверяем в ответе для избежания CSRF атак.
    private val uniqueState = UUID.randomUUID().toString()

    private lateinit var responseCode: String
    private lateinit var browser: WebView
    private lateinit var callback: IAuthorizationEventHandler

    // Подготавливаем URL
    val uri = Uri.parse(oauthSettingsProvider.authUrl)
      .buildUpon()
      .appendQueryParameter("client_id", oauthSettingsProvider.clientId)
      .appendQueryParameter("code_challenge_method", "S256")
      .appendQueryParameter("code_challenge", oauthSettingsProvider.codeChallenge)
      .appendQueryParameter("redirect_uri", oauthSettingsProvider.redirectUri)
      .appendQueryParameter("response_type", oauthSettingsProvider.responseType)
      .appendQueryParameter("scope", oauthSettingsProvider.scope)
      .appendQueryParameter("state", uniqueState)
      .build()

    suspend fun checkAuthorization(
      webView: WebView,
      callback: IAuthorizationEventHandler
    ) {
      browser = webView
      this.callback = callback
      val accessToken = userManager.getAccessToken(context)
      val refreshToken = userManager.getRefreshToken(context)
      try {
	if (accessToken != null && refreshToken != null) {
	  if (userManager.isExpiredToken(context)) {
	    if (!refreshToken()) startAuthentication()
	  } else {
	    this.callback.onTokenSuccess()
	  }
	} else {
	  this.callback.onAuthorizationBegin()
	  startAuthentication()
	}
      } catch (e: Exception) {
	e.message?.let { Log.e("Fatal Connection", it) }
	throw DeniedByServerException("Не удалось подключиться к серверу авторизации.")
      }
    }

    private fun getAuthApi(): IAuthenticationApiService {
      val gsonBuilder = GsonBuilder()
      gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      val client = OkHttpClient.Builder()
      with(client) {
	sslSocketFactory(
	  UnsafeConnection.getSslSocketFactory(),
	  UnsafeConnection.getTrustAllCerts()[0] as X509TrustManager
	)
	hostnameVerifier { _, _ -> true }
      }
      val retrofit = Retrofit.Builder()
	.baseUrl(oauthSettingsProvider.tokenUrl)
	.client(client.build())
	.addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
	.build()
      return retrofit.create(IAuthenticationApiService::class.java)
    }


    suspend fun refreshToken() = coroutineScope {
      val refreshToken = userManager.getRefreshToken(context)
      if (refreshToken != null) {
	val data = async(Dispatchers.Main) {
	  getAuthApi().refreshToken(
	    oauthSettingsProvider.clientId,
	    oauthSettingsProvider.clientSecret,
	    oauthSettingsProvider.grantTypeRefresh,
	    refreshToken
	  )
	}
	val oauthData = data.await()
	if (oauthData.isSuccessful) {
	  val body = oauthData.body()
	  if (body != null) {
	    with(userManager) {
	      saveToken(
		context,
		body.access_token,
		body.id_token,
		body.refresh_token
	      )
	      setExpirationDate(context, body.expires_in)
	    }
	    Log.d(
	      "success",
	      "Токены обновлены и сохранены! ${body.access_token} ; ${body.refresh_token}"
	    )
	    try {
	      callback.onTokenSuccess()
	    } catch (e: Exception) {
	    }
	  }
	}
	return@coroutineScope oauthData.isSuccessful
      } else {
	false
      }
    }

    private fun getOAuthData() {
      GlobalScope.launch(Dispatchers.Main) {
	try {
	  val data = getAuthApi().getToken(
	    oauthSettingsProvider.clientId,
	    oauthSettingsProvider.clientSecret,
	    responseCode,
	    oauthSettingsProvider.grantType,
	    oauthSettingsProvider.redirectUri,
	    oauthSettingsProvider.codeVerifier
	  )
	  if (data.isSuccessful) {
	    val body = data.body()
	    if (body != null) {
	      with(userManager) {
		saveToken(
		  context,
		  body.access_token,
		  body.id_token,
		  body.refresh_token
		)
		setExpirationDate(context, body.expires_in)
		saveUserId(context, body.id_token)
		Log.d(
		  "success",
		  "Токены получены и сохранены! ${body.access_token} ; ${body.refresh_token} ; ${body.expires_in}"
		)
		callback.onTokenAcquired()
		Log.d("user_info", userManager.getUserId(context))
	      }
	    }
	  }
	} catch (e: Exception) {
	  e.message?.let { Log.e("fatal", it) }
	}

      }
    }

    private fun startAuthentication() {
      browser.visibility = View.VISIBLE
      // TODO Убрать когда будут сертификаты.
      browser.webViewClient = WvClient()
      browser.settings.useWideViewPort = true
      browser.settings.javaScriptEnabled = true
      browser.loadUrl(uri.toString())
      Log.d("Request Access Token", "Запрос на получение токена.")
    }

    private inner class WvClient : WebViewClient() {
      // TODO Убрать когда будут сертификаты.
      override fun onReceivedSslError(
	view: WebView?,
	handler: SslErrorHandler?,
	error: SslError?
      ) {
	handler?.proceed()
      }

      override fun shouldOverrideUrlLoading(
	view: WebView?,
	request: WebResourceRequest?
      ): Boolean {
	view?.clearCache(true)
	request?.let {
	  Log.d("browser", "Browser Opened")
	  if (request.url.toString().startsWith(oauthSettingsProvider.redirectUri)) {
	    val responseState = request.url.getQueryParameter("state")
	    Log.d("url", request.url.toString())
	    if (responseState == uniqueState) {
	      request.url.getQueryParameter("code")?.let { code ->
		Log.d(
		  "Success",
		  "Авторизация прошла успешно."
		)
		responseCode = code
		getOAuthData()
		Log.d("code", responseCode)
	      } ?: run {
		// Если юзер нажал отмену.
		Log.d("problems", "что-то пошло не так.")
	      }
	    }
	  }
	}
	return super.shouldOverrideUrlLoading(view, request)
      }
    }
  }

  private interface IAuthenticationApiService {
    @POST("token")
    @FormUrlEncoded
    suspend fun getToken(
      @Field("client_id") client_id: String,
      @Field("client_secret") client_secret: String,
      @Field("code") code: String,
      @Field("grant_type") grant_type: String,
      @Field("redirect_uri") redirect_uri: String,
      @Field("code_verifier") code_verifier: String
    ): Response<TokenResponse>

    @POST("token")
    @FormUrlEncoded
    suspend fun refreshToken(
      @Field("client_id") client_id: String,
      @Field("client_secret") client_secret: String,
      @Field("grant_type") grant_type: String,
      @Field("refresh_token") refresh_token: String,
    ): Response<TokenResponse>

    data class TokenResponse(
      val access_token: String,
      val id_token: String,
      val refresh_token: String,
      val expires_in: Long,
      val token_type: String,
      val scope: String,
    )
  }
}