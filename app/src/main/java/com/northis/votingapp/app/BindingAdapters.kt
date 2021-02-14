package com.northis.votingapp.app


import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.northis.votingapp.R
import com.northis.votingapp.voting.VotingModel
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("app:timeHasLeft")
fun setTimeHasLeft(view: TextView, date: Date?) {
  if (date != null) {
    if (date.time < Date().time) {
      view.text = ""
      view.setBackgroundResource(R.color.ended)
    } else view.text = getTimeHasLeft(date)
  } else view.text = "NA"
}

@BindingAdapter("app:totalVotingVote")
fun setTotalVoteAmount(view: TextView, amount: Int) {
  view.text = amount.toString()
}

@BindingAdapter("app:hasUserVoted")
fun setColorBackground(view: TextView, hasUserVoted: Boolean) {
  if (hasUserVoted) view.setBackgroundResource(R.color.voted) else view.setBackgroundResource(R.color.notVoted)
}

@BindingAdapter("app:busyMode")
fun setBusyLoader(view: View, flag: LiveData<Boolean>) {
  if (flag.value == true) {
    when (true) {
      view is ProgressBar -> view.visibility = View.VISIBLE
      else -> view.visibility = View.GONE
    }
  } else {
    when (true) {
      view is ProgressBar -> view.visibility = View.GONE
      else -> view.visibility = View.VISIBLE
    }
  }
}

@BindingAdapter("app:progressBar", "app:votesPercentage", "app:totalVotes", requireAll = true)
fun setColorProgressBar(view: TextView, flag: Boolean, votingSpeech: VotingModel.VotingSpeech, totalVotes: Int) {
  val percent = ((votingSpeech.Users.size.toDouble() / totalVotes.toDouble()) * 100).toInt()
  view.background = null
  val layoutParamsMP = view.layoutParams
  layoutParamsMP.width = -1
  view.layoutParams = layoutParamsMP
  view.post {
    var width = view.width
    val layoutParams = view.layoutParams
    layoutParams.width = width * percent / 100
    view.layoutParams = layoutParams
  }
  when (true) {
    flag && votingSpeech.HasUserVoted -> view.setBackgroundResource(R.color.userVoted)
    flag && !votingSpeech.HasUserVoted -> view.setBackgroundResource(R.color.userNotVoted)
    !flag -> view.background = null
  }


}

@BindingAdapter(value = ["app:votesPercentage", "app:totalVotes"], requireAll = true)
fun setSpeechVotePercentage(view: TextView, votingSpeech: VotingModel.VotingSpeech, totalVotes: Int) {
  val percent = ((votingSpeech.Users.size.toDouble() / totalVotes.toDouble()) * 100).toInt()
  if (totalVotes != 0) view.text = "$percent %" else view.text = "0 %"
}

@BindingAdapter("app:VotedVisibility")
fun setViewVisibility(view: View, flag: Boolean) {
  if (flag) view.visibility = View.VISIBLE else view.visibility = View.INVISIBLE
}

@BindingAdapter("app:votingEndDate")
fun setVotingEndDate(view: TextView, date: Date?) {
  if (date == null) view.text = "Голосование не начато" else view.text = "До " + SimpleDateFormat("dd.MM.yyy").format(date)
}

@BindingAdapter("app:totalSpeechVote")
fun setSpeechTotalVotes(view: TextView, amount: Int) {
  view.text = amount.toString()
}

private fun getTimeHasLeft(date: Date): String {
  val currentDate = Date().time
  val timeLeft = date.time - currentDate
  val d = timeLeft / 86400000
  val h = d / 3600000
  val m = h / 60000
  return when (true) {
    timeLeft > 0 -> "$d Д."
    else -> if (h > 0) "$h Ч." else "$m Мин."
  }
}