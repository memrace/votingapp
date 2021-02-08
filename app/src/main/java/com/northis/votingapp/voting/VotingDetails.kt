package com.northis.votingapp.voting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.databinding.FragmentVotingDetailsBinding

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
    viewModel.loading.value = true
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.loadVoting().observe(viewLifecycleOwner, {
      Log.d("data", it.toString())
    })
  }

}