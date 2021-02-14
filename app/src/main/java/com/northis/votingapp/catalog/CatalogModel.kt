package com.northis.votingapp.catalog

import com.northis.votingapp.app.CommonModel
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

private const val SERVICE = "speechcatalog/"
private const val SPEECH = "speeches/"
private const val CALENDAR = "calendar/"

class CatalogModel @Inject constructor(private val catalogApi: ICatalogApi, private val commonModel: CommonModel) {

  suspend fun loadSpeech(uuid: String): Response<CommonModel.Speech> {
    return commonModel.handleAuthorityResponse {
      catalogApi.getSpeech(uuid)
    }
  }

  suspend fun loadSpeeches(
    skip: Int? = null,
    status: String? = SpeechStatus().InCatalog,
    count: Int? = null,
    minCreateDate: Date? = null,
    maxCreateDate: Date? = null,
    minEndDate: Date? = null,
    maxEndDate: Date? = null,
    executorId: String? = null,
    creatorId: String? = null,
    theme: String? = null
  ): Response<Speeches> {
    return commonModel.handleAuthorityResponse {
      catalogApi.getSpeeches(sort = "theme", skip, status, count, minCreateDate, maxCreateDate, minEndDate, maxEndDate, executorId, creatorId, theme)
    }
  }

  suspend fun createSpeech(newSpeech: CommonModel.Speech): Response<CommonModel.Speech> {
    return commonModel.handleAuthorityResponse {
      catalogApi.createSpeech(newSpeech)
    }
  }

  suspend fun setExecutor(speechId: String, executorId: String, speechDate: Date): Response<Unit> {
    return commonModel.handleAuthorityResponse {
      catalogApi.changeSpeechExecutor(speechId, executorId, speechDate)
    }
  }

  suspend fun updateSpeech(speechId: String, theme: String, description: String): Response<Unit> {
    return commonModel.handleAuthorityResponse {
      catalogApi.updateSpeech(speechId, description, theme)
    }
  }

  // Todo обновить
  suspend fun getCalendar(minEndDate: Date, maxEndDate: Date): Response<ArrayList<CommonModel.Speech>> {
    return commonModel.handleAuthorityResponse {
      catalogApi.getCalendarSpeeches(minEndDate, maxEndDate)
    }
  }

  suspend fun createSpeechInCalendar(speech: CommonModel.Speech, executorId: String, speechDate: Date): Response<CommonModel.Speech> {
    return commonModel.handleAuthorityResponse {
      catalogApi.createSpeechInCalendar(speech, executorId, speechDate.toString())
    }
  }

  suspend fun deleteSpeech(speechId: String): Response<Unit> {
    return commonModel.handleAuthorityResponse {
      catalogApi.deleteSpeech(speechId)
    }
  }


  data class SpeechStatus(
    val InCalendar: String = "InCalendar",
    val InCatalog: String = "InCatalog",
    val InVoting: String = "InVoting"
  )

  data class Speeches(
    val Speeches: ArrayList<CommonModel.Speech>,
    val SpeechesCount: Int
  )

  interface ICatalogApi {

    @GET("$SERVICE$SPEECH{uuid}")
    suspend fun getSpeech(@Path("uuid") uuid: String): Response<CommonModel.Speech>

    @GET("$SERVICE$SPEECH")
    suspend fun getSpeeches(
      @Query("sort") sort: String,
      @Query("skip") skip: Int? = null,
      @Query("status") status: String? = null,
      @Query("count") count: Int? = 10,
      @Query("minCreateDate") minCreateDate: Date? = null,
      @Query("maxCreateDate") maxCreateDate: Date? = null,
      @Query("minEndDate") minEndDate: Date? = null,
      @Query("maxEndDate") maxEndDate: Date? = null,
      @Query("executor") executorId: String? = null,
      @Query("creator") creatorId: String? = null,
      @Query("theme") theme: String? = null
    ): Response<Speeches>

    @Multipart
    @PATCH("$SERVICE$SPEECH{uuid}/executor")
    suspend fun changeSpeechExecutor(
      @Path("uuid") uuid: String,
      @Part("ExecutorId") executorId: String,
      @Part("SpeechDate") speechDate: Date
    ): Response<Unit>

    @POST("$SERVICE$SPEECH")
    suspend fun createSpeech(@Body speech: CommonModel.Speech): Response<CommonModel.Speech>

    @POST("$SERVICE$SPEECH$CALENDAR")
    suspend fun createSpeechInCalendar(@Body speech: CommonModel.Speech, @Part("executorId") executorId: String, @Part("speechDate") speechDate: String): Response<CommonModel.Speech>

    @DELETE("$SERVICE$SPEECH{uuid}")
    suspend fun deleteSpeech(@Path("uuid") speechId: String): Response<Unit>

    @GET("$SERVICE$CALENDAR")
    suspend fun getCalendarSpeeches(
      @Query("minEndDate") minEndDate: Date,
      @Query("maxEndDate") maxEndDate: Date
    ): Response<ArrayList<CommonModel.Speech>>

    @Multipart
    @PATCH("$SERVICE$SPEECH/{uuid}")
    suspend fun updateSpeech(
      @Path("uuid") speechId: String,
      @Part("Description") description: String,
      @Part("Theme") theme: String
    ): Response<Unit>
  }
}

