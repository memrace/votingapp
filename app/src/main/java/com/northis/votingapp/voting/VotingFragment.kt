package com.northis.votingapp.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.northis.votingapp.R
import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.databinding.FragmentVotingBinding
import com.northis.votingapp.databinding.VotingListViewHolderBinding
import java.util.*


class VotingFragment : Fragment() {
  private var _binding: FragmentVotingBinding? = null
  private val binding get() = _binding!!
  private lateinit var viewModel: VotingViewModel
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentVotingBinding.inflate(layoutInflater, container, false)
    viewModel = (activity as AppActivity).votingViewModel
    binding.viewmodel = viewModel
    binding.lifecycleOwner = this
    binding.executePendingBindings()
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.loading.value = true
    viewModel.loadVotingList().observe(viewLifecycleOwner, {
      viewModel.loading.value = false
      if (it != null) {
        binding.rvVotingList.apply {
          adapter = VotingListAdapter(it)
          layoutManager = LinearLayoutManager(context)
          setHasFixedSize(true)
        }
      }
    })
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }


  private inner class VotingListAdapter(private val votingList: ArrayList<VotingModel.Voting>) : RecyclerView.Adapter<VotingListAdapter.VotingListViewHolder>() {
    private inner class VotingListViewHolder(private val itemBinding: VotingListViewHolderBinding) : RecyclerView.ViewHolder(itemBinding.root) {
      val layout: MaterialCardView by lazy { itemBinding.cvVoting }
      fun bind(data: VotingModel.Voting) {
	itemBinding.voting = data
	itemBinding.executePendingBindings()
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingListViewHolder {
      val view = VotingListViewHolderBinding.inflate(layoutInflater, parent, false)
      return VotingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: VotingListViewHolder, position: Int) {
      val voting = votingList[position]
      holder.bind(voting)
      holder.layout.setOnClickListener {
	viewModel.votingDetails = voting
	viewModel.loading.value = false
	(activity as AppActivity).navController.navigate(R.id.action_votingFragment_to_votingDetailes)
      }
    }

    override fun getItemCount(): Int {
      return votingList.size
    }
  }
}
