package com.northis.votingapp.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.databinding.FragmentVotingDetailsBinding
import com.northis.votingapp.databinding.SpeechInVotingListViewHolderBinding

class VotingDetails : Fragment() {
  private var _binding: FragmentVotingDetailsBinding? = null
  private val binding get() = _binding!!
  private lateinit var viewModel: VotingViewModel
  private lateinit var votingSpeechesAdapter: VotingSpeechesAdapter
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentVotingDetailsBinding.inflate(layoutInflater, container, false)
    viewModel = (activity as AppActivity).votingViewModel
    binding.apply {
      viewmodel = viewModel
      lifecycleOwner = this@VotingDetails
      executePendingBindings()
    }
    votingSpeechesAdapter = VotingSpeechesAdapter()
    (activity as AppActivity).showSearch(false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.loading.value = true
    updateUI()
    viewModel.loadVoting().observe(viewLifecycleOwner, {
      viewModel.loading.value = false
      votingSpeechesAdapter.voting = it
      binding.rvVotingDetails.apply {
	adapter = votingSpeechesAdapter
	layoutManager = LinearLayoutManager(context)
	setHasFixedSize(true)
      }
    })
  }


  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun updateUI() {
    viewModel.updateUi.observe(viewLifecycleOwner, { it ->
      if (it) {
	viewModel.loadVoting().observe(viewLifecycleOwner, {
	  votingSpeechesAdapter.voting = it
	  votingSpeechesAdapter.notifyDataSetChanged()
	  viewModel.updateUi.value = false
	})
      }
    })
  }

  private inner class VotingSpeechesAdapter() : RecyclerView.Adapter<VotingSpeechesAdapter.VotingSpeechesViewHolder>() {
    lateinit var voting: VotingModel.Voting

    private inner class VotingSpeechesViewHolder(private val itemBinding: SpeechInVotingListViewHolderBinding) : RecyclerView.ViewHolder(itemBinding.root) {
      val voteButton: RadioButton = itemBinding.rbVote
      val container: MaterialCardView = itemBinding.cvSpeechInVoting
      fun bind(speech: VotingModel.VotingSpeech) {
	itemBinding.votingSpeech = speech
	itemBinding.voting = voting
	itemBinding.executePendingBindings()
      }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingSpeechesViewHolder {
      val view = SpeechInVotingListViewHolderBinding.inflate(layoutInflater, parent, false)
      return VotingSpeechesViewHolder(view)
    }

    override fun onBindViewHolder(holder: VotingSpeechesViewHolder, position: Int) {
      voting.VotingSpeeches[position].also { it ->
	val speechId = it.Speech.SpeechId.toString()
	holder.bind(it)
	with(holder.container) {
	  val btn = holder.voteButton
	  btn.isChecked = it.HasUserVoted
	  setOnClickListener {
	    if (btn.isChecked) {
	      viewModel.unVoteForSpeech(speechId).observe(viewLifecycleOwner, { it ->
		val message: String = if (it) "Голос отменен" else "Произошла ошибки..."
		Toast.makeText(context, "$message", Toast.LENGTH_SHORT).also { it.show() }
		viewModel.updateUi.value = true
	      })
	    } else {
	      viewModel.voteForSpeech(speechId).observe(viewLifecycleOwner, { it ->
		val message: String = if (it) "Голос принят" else "Произошла ошибки..."
		Toast.makeText(context, "$message", Toast.LENGTH_SHORT).also { it.show() }
		viewModel.updateUi.value = true
	      })
	    }
	  }
	}
      }
    }

    override fun getItemCount(): Int {
      return voting.VotingSpeeches.size
    }
  }
}