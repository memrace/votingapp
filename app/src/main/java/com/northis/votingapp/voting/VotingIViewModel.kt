package com.northis.votingapp.voting

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.northis.votingapp.app.CommonModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class VotingIViewModel(private val votingModel: VotingModel, private val commonModel: CommonModel) : ViewModel() {

  val updateUi: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  val loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  var votingDetails: VotingModel.Voting? = null
  val voting get() = votingDetails!!

  fun voteForSpeech(speechId: String) = liveData(Dispatchers.IO) {
    val result = commonModel.handleAuthorityResponse {
      votingModel.addVote(voting.VotingId.toString(), VotingModel.UserVote(speechId, commonModel.userId))
    }
    emit(result.isSuccessful)
  }

  fun unVoteForSpeech(speechId: String) = liveData(Dispatchers.IO) {
    val result = commonModel.handleAuthorityResponse {
      votingModel.removeVote(voting.VotingId.toString(), commonModel.userId)
    }
    emit(result.isSuccessful)
  }

  fun loadVotingList() = liveData(Dispatchers.IO) {
    val result = commonModel.handleAuthorityResponse {
      votingModel.getVotingList()
    }
    emit(result.body()!!)
  }

  fun loadVoting() = liveData(Dispatchers.IO) {
    val result = commonModel.handleAuthorityResponse {
      votingModel.getVoting(voting.VotingId.toString())
    }
    emit(result.body()!!)
  }

  fun addSpeechToVoting(speechId: String) = liveData(Dispatchers.IO) {
    val result = votingModel.addSpeech(voting.VotingId.toString(), speechId)
    emit(result)
  }
}

class VotingViewModelFactory @Inject constructor(
  application: Application,
  private val votingModel: VotingModel,
  private val commonModel: CommonModel
) : ViewModelProvider.AndroidViewModelFactory(application) {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return VotingIViewModel(votingModel, commonModel) as T
  }
}