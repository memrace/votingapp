package com.northis.votingapp.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.northis.votingapp.R
import com.northis.votingapp.app.AppActivity

class ScheduleFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    (activity as AppActivity).showSearch(true)
    return inflater.inflate(R.layout.fragment_schedule, container, false)
  }

}