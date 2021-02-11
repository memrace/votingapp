package com.northis.votingapp.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.databinding.FragmentVotingDetailsBinding
import com.northis.votingapp.databinding.SpeechInVotingListViewHolderBinding

class VotingDetails : Fragment() {
  private var _binding: FragmentVotingDetailsBinding? = null
  private val binding get() = _binding!!
  private lateinit var viewModel: VotingViewModel
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
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.loading.value = true
    viewModel.loadVoting().observe(viewLifecycleOwner, {
      viewModel.loading.value = false
      binding.tvEndDate.text = if (it.EndDate != null) it.EndDate.toString() else "Голосование не начато"
      binding.rvVotingDetails.apply {
        adapter = VotingSpeechesAdapter(it)
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
      }
    })
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private inner class VotingSpeechesAdapter(private val voting: VotingModel.Voting) : RecyclerView.Adapter<VotingSpeechesAdapter.VotingSpeechesViewHolder>() {
    private inner class VotingSpeechesViewHolder(private val itemBinding: SpeechInVotingListViewHolderBinding) : RecyclerView.ViewHolder(itemBinding.root) {
      fun bind(speech: VotingModel.VotingSpeech) {
	itemBinding.votingSpeech = speech
	itemBinding.executePendingBindings()
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingSpeechesViewHolder {
      val view = SpeechInVotingListViewHolderBinding.inflate(layoutInflater, parent, false)
      return VotingSpeechesViewHolder(view)
    }

    override fun onBindViewHolder(holder: VotingSpeechesViewHolder, position: Int) {
      val speech = voting.VotingSpeeches[position]
      holder.bind(speech)
//      holder.apply {
//	tvTheme.text = speech.Speech.Theme
//	tvCreator.text = "(${speech.Speech.Creator.FirstName} ${speech.Speech.Creator.LastName})"
//	when (speech.Users.size) {
//	  0 -> {
//	  }
//	  1 -> {
//	    Glide
//	      .with(context!!)
//	      .load(viewModel.imageResourceUrl + speech.Users[0].ImageUrl)
//	      .circleCrop()
//	      .placeholder(R.drawable.ic_person_24px)
//	      .into(ivUser1)
//	  }
//	  2 -> {
//	    Glide
//	      .with(context!!)
//	      .load(viewModel.imageResourceUrl + speech.Users[0].ImageUrl)
//	      .circleCrop()
//	      .placeholder(R.drawable.ic_person_24px)
//	      .into(ivUser1)
//	    Glide
//	      .with(context!!)
//	      .load(viewModel.imageResourceUrl + speech.Users[1].ImageUrl)
//	      .circleCrop()
//	      .placeholder(R.drawable.ic_person_24px)
//	      .into(ivUser2)
//	  }
//	  else -> {
//	    Glide
//	      .with(context!!)
//	      .load(viewModel.imageResourceUrl + speech.Users[0].ImageUrl)
//	      .circleCrop()
//	      .placeholder(R.drawable.ic_person_24px)
//	      .into(ivUser1)
//	    Glide
//	      .with(context!!)
//	      .load(viewModel.imageResourceUrl + speech.Users[1].ImageUrl)
//	      .circleCrop()
//	      .placeholder(R.drawable.ic_person_24px)
//	      .into(ivUser2)
//	    Glide
//	      .with(context!!)
//	      .load(viewModel.imageResourceUrl + speech.Users[2].ImageUrl)
//	      .circleCrop()
//	      .placeholder(R.drawable.ic_person_24px)
//	      .into(ivUser3)
//	  }
//	}
//	tvTotalVotes.text = speech.Users.size.toString()
//
//      }
    }

    override fun getItemCount(): Int {
      return voting.VotingSpeeches.size
    }
  }
}