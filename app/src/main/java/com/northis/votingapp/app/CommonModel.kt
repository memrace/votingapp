package com.northis.votingapp.app

import com.northis.votingapp.authorization.AuthorizationModel
import com.northis.votingapp.authorization.IAuthorizationEventHandler
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*
import javax.inject.Inject


private const val SERVICE = "profiles/"

class CommonModel @Inject constructor(private val profileApi: IProfileApi, private val authorizationModel: AuthorizationModel) {
  val imageResourceUrl: String get() = "https://192.168.100.8:5001"
  suspend fun loadUser(id: String): IdentityUser? {
    return profileApi.getUser(id).body()
  }

  suspend fun loadUsers(): ArrayList<IdentityUser>? {
    return profileApi.getUsers().body()
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
    val Status: String?,
    val SpeechDate: Date?,
    val SourceLinks: String?,
    val Executor: IdentityUser?
  )

  interface IProfileApi {
    @GET("$SERVICE{uuid}")
    suspend fun getUser(
      @Path("uuid") uuid: String
    ): Response<IdentityUser>

    @GET(SERVICE)
    suspend fun getUsers(): Response<ArrayList<IdentityUser>>
  }
}