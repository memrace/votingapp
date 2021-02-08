package com.northis.votingapp.voting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.northis.votingapp.R
import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.databinding.FragmentVotingBinding
import java.util.*


class VotingFragment : Fragment(), OnVotingClickListener {
  private var _binding: FragmentVotingBinding? = null
  private val binding get() = _binding!!
  private lateinit var viewModel: VotingViewModel
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentVotingBinding.inflate(layoutInflater, container, false)
    viewModel = (activity as AppActivity).votingViewModel
    viewModel.loading.value = true
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.loading.observe(viewLifecycleOwner, {
      when (it) {
	true -> binding.pbLoading.visibility = View.VISIBLE
	else -> binding.pbLoading.visibility = View.GONE
      }
    })
    viewModel.loadVotingList().observe(viewLifecycleOwner, {
      Log.d("data", it.toString())
      if (it != null) {
	viewModel.loading.value = false
	binding.rvVotingList.adapter = VotingListAdapter(it)
	binding.rvVotingList.layoutManager = LinearLayoutManager(context)
      }
    })
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }


  private inner class VotingListAdapter(private val votingList: ArrayList<VotingModel.Voting>) : RecyclerView.Adapter<VotingListAdapter.VotingListViewHolder>() {
    private inner class VotingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val layout: LinearLayout by lazy { itemView.findViewById(R.id.l_voting_list) }
      val votesAmount: TextView by lazy { itemView.findViewById(R.id.tv_votes_amount) }
      val votingTitle: TextView by lazy { itemView.findViewById(R.id.tv_voting_title) }
      val votingDays: TextView by lazy { itemView.findViewById(R.id.tv_voting_days) }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingListViewHolder {
      val view = LayoutInflater.from(context).inflate(R.layout.voting_list_view_holder, parent, false)
      return VotingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: VotingListViewHolder, position: Int) {
      val voting = votingList[position]
      holder.itemView.setOnClickListener {
	onClick(voting)
      }
      holder.votingTitle.text = voting.Title
      if (voting.HasUserVoted) holder.votesAmount.setBackgroundColor(resources.getColor(R.color.voted, null)) else holder.votesAmount.setBackgroundColor(resources.getColor(R.color.notVoted, null))
      holder.votesAmount.text = voting.TotalVotes.toString()
      if (voting.EndDate != null) {
	if (voting.EndDate < Date()) {
	  holder.votingDays.text = ""
	  holder.votingDays.setBackgroundColor(resources.getColor(R.color.ended, null))
	} else holder.votingDays.text = getTimeHasLeft(voting.EndDate)
      } else holder.votingDays.text = "NA"

    }

    override fun getItemCount(): Int {
      return votingList.size
    }
  }

  override fun onClick(data: VotingModel.Voting) {
    viewModel.votingDetails = data
    Log.d("click", "click")
    (activity as AppActivity).navController.navigate(R.id.action_votingFragment_to_votingDetailes)
  }
}

private interface OnVotingClickListener {
  fun onClick(data: VotingModel.Voting)
}
