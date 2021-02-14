package com.northis.votingapp.voting

import com.northis.votingapp.app.CommonModel
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

private const val SERVICE = "speechvoting/"
private const val VOTING = "votings/"
private const val VOTE = "votes/"
private const val WINNER = "winner/"

class VotingModel @Inject constructor(private val votingApi: IVotingApi) {


  suspend fun addSpeech(votingId: String, speechId: String): Response<Unit> {
    return votingApi.addSpeech(votingId, speechId)
  }

  suspend fun addVote(votingId: String, vote: UserVote): Response<Unit> {
    return votingApi.addVote(votingId, vote)
  }

  suspend fun getVoting(votingId: String): Response<Voting> {
    return votingApi.getVoting(votingId)
  }

  suspend fun getVotingList(): Response<ArrayList<Voting>> {
    return votingApi.getVotingList()
  }

  suspend fun removeVote(votingId: String, userId: String): Response<Unit> {
    return votingApi.removeVote(votingId, userId)
  }

  suspend fun getWinner(votingId: String): Response<CommonModel.Speech> {
    return votingApi.getWinner(votingId)
  }


  interface IVotingApi {
    @POST("$SERVICE$VOTING{uuid}/speeches")
    suspend fun addSpeech(@Path("uuid") votingId: String, @Body speechId: String): Response<Unit>

    @POST("$SERVICE$VOTING{uuid}/$VOTE")
    suspend fun addVote(@Path("uuid") votingId: String, @Body vote: UserVote): Response<Unit>

    @GET("$SERVICE$VOTING{uuid}")
    suspend fun getVoting(@Path("uuid") votingId: String): Response<Voting>

    @GET("$SERVICE$VOTING")
    suspend fun getVotingList(): Response<ArrayList<Voting>>

    @DELETE("$SERVICE$VOTING{uuid}/$VOTE{userId}")
    suspend fun removeVote(
      @Path("uuid") votingId: String,
      @Path("userId") userId: String
    ): Response<Unit>

    @GET("$SERVICE$VOTING{uuid}/$WINNER")
    suspend fun getWinner(@Path("uuid") votingId: String): Response<CommonModel.Speech>

  }

  data class Voting(
    val Creator: CommonModel.IdentityUser,
    val EndDate: Date?,
    val HasUserVoted: Boolean,
    val StartDate: Date,
    val Title: String,
    val TotalVotes: Int,
    val VotingId: UUID,
    val VotingSpeeches: ArrayList<VotingSpeech>,
    val HasEnded: Boolean? = if (EndDate == null) null else EndDate < Date()
  )

  data class VotingSpeech(
    val Speech: CommonModel.Speech,
    val HasUserVoted: Boolean,
    val Users: ArrayList<CommonModel.IdentityUser>
  )

  data class UserVote(
    val SpeechId: String,
    val UserProfileId: String
  )

}