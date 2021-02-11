package com.northis.votingapp.app


import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.northis.votingapp.R
import java.util.*


@BindingAdapter("app:timeHasLeft")
fun setTimeHasLeft(view: TextView, date: Date?) {
  if (date != null) {
    if (date < Date()) {
      view.text = ""
      view.setBackgroundResource(R.color.ended)
    } else view.text = getTimeHasLeft(date)
  } else view.text = "NA"
}

@BindingAdapter("app:totalVoteAmount")
fun setTotalVoteAmount(view: TextView, amount: Int) {
  view.text = amount.toString()
}

@BindingAdapter("app:hasUserVoted")
fun setColorBackground(view: TextView, flag: Boolean) {
  if (flag) view.setBackgroundResource(R.color.voted) else view.setBackgroundResource(R.color.notVoted)
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