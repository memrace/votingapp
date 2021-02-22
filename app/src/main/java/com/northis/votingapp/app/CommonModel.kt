package com.northis.votingapp.app

import android.content.Context
import com.northis.votingapp.authorization.AuthorizationModel
import com.northis.votingapp.authorization.IUserManager
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*
import javax.inject.Inject


private const val SERVICE = "profiles/"

class CommonModel @Inject constructor(private val context: Context, private val profileApi: IProfileApi, private val authorizationModel: AuthorizationModel) {
  val userId: String get() = IUserManager.instance.getUserId(context)
  suspend fun loadUser(): IdentityUser {
    return handleAuthorityResponse {
      profileApi.getUser(userId)
    }.body()!!
  }

  suspend fun loadUsers(): ArrayList<IdentityUser>? {
    return handleAuthorityResponse {
      profileApi.getUsers()
    }.body()
  }

  suspend fun <T> handleAuthorityResponse(request: suspend () -> Response<T>): Response<T> {
    val response = request.invoke()
    return if (response.code() == 401) {
      authorizationModel.authorizationService.refreshToken()
      return request.invoke()
    } else response
  }

  data class IdentityUser(
    val Email: String,
    val FirstName: String,
    val Id: String,
    val ImageUrl: String,
    val LastName: String,
    val UserName: String
  )

  data class Speech(
    val SpeechId: UUID,
    val CreateDate: Date,
    val Creator: IdentityUser,
    val Description: String,
    val Theme: String,
    private val Status: String?,
    val SpeechDate: Date?,
    val SourceLinks: String?,
    val Executor: IdentityUser?,
    val IsCompleted: Boolean?
  ) {
    val SpeechStatus
      get() = when (Status) {
        SpeechStatuses.InCatalog -> "Каталог"
        SpeechStatuses.InCalendar -> "Календарь"
        SpeechStatuses.InVoting -> "Голосование"
	else -> "NaN"
      }

  }

  class SpeechStatuses {
    companion object {
      const val InCalendar: String = "InCalendar"
      const val InCatalog: String = "InCatalog"
      const val InVoting: String = "InVoting"
    }
  }

  interface IProfileApi {
    @GET("$SERVICE{uuid}")
    suspend fun getUser(
      @Path("uuid") uuid: String
    ): Response<IdentityUser>

    @GET(SERVICE)
    suspend fun getUsers(): Response<ArrayList<IdentityUser>>
  }
}