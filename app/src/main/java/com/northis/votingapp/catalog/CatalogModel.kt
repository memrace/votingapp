package com.northis.votingapp.catalog

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.northis.votingapp.app.CommonModel
import com.northis.votingapp.catalog.pagination.SpeechesDataSource
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

  fun loadSpeeches(query: SpeechQuery) =
    Pager(
      config =
      PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = true),
      pagingSourceFactory = { SpeechesDataSource(this, query) }
    ).liveData


  suspend fun getSpeeches(
    skip: Int? = null,
    take: Int? = null,
    query: SpeechQuery
  ): Response<Speeches> {
    return commonModel.handleAuthorityResponse {
      queryHandler(query, skip, take)
    }
  }

  private suspend fun queryHandler(
    query: SpeechQuery, skip: Int? = null,
    take: Int? = null,
  ): Response<Speeches> {
    val queryKey = query.getQuery().keys.first()
    val queryValue = query.getQuery().values.first()
    return when (queryKey) {
      SpeechQueries.MINCREATEDATE -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take, minCreateDate = queryValue)
      SpeechQueries.MAXCREATEDATE -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take, maxCreateDate = queryValue)
      SpeechQueries.MINENDDATE -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take, minEndDate = queryValue)
      SpeechQueries.MAXENDDATE -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take, maxEndDate = queryValue)
      SpeechQueries.EXECUTOR -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take, executorId = queryValue)
      SpeechQueries.CREATOR -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take, creatorId = queryValue)
      SpeechQueries.THEME -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take, theme = queryValue)
      else -> catalogApi.getSpeeches(sort = "theme", skip = skip, take = take)
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

  class SpeechQuery(key: SpeechQueries, value: String) {
    private val query: Map<SpeechQueries, String> = mapOf(Pair(key, value))
    fun getQuery(): Map<SpeechQueries, String> {
      return query
    }
  }

  enum class SpeechQueries(val query: String) {
    MINCREATEDATE("minCreateDate"),
    MAXCREATEDATE("maxCreateDate"),
    MINENDDATE("minEndDate"),
    MAXENDDATE("maxEndDate"),
    EXECUTOR("executor"),
    CREATOR("creator"),
    THEME("theme"),
    DEFAULT("default")
  }

  data class Speeches(
    val Speeches: List<CommonModel.Speech>,
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
      @Query("take") take: Int? = 10,
      @Query("minCreateDate") minCreateDate: String? = null,
      @Query("maxCreateDate") maxCreateDate: String? = null,
      @Query("minEndDate") minEndDate: String? = null,
      @Query("maxEndDate") maxEndDate: String? = null,
      @Query("executor") executorId: String? = null,
      @Query("creator") creatorId: String? = null,
      @Query("theme") theme: String? = null,
      @Query("completed") completed: String? = null
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

