package com.northis.votingapp.catalog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.app.CommonModel
import com.northis.votingapp.databinding.FragmentCatalogBinding
import com.northis.votingapp.databinding.SpeechesViewHolderBinding


class CatalogFragment : Fragment() {
  private var _binding: FragmentCatalogBinding? = null
  val binding get() = _binding!!
  private lateinit var viewModel: CatalogViewModel
  private lateinit var adapter: CatalogSpeechesAdapter
  private lateinit var recyclerView: RecyclerView
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentCatalogBinding.inflate(layoutInflater, container, false)
    (activity as AppActivity).showSearch(true)
    viewModel = (activity as AppActivity).catalogViewModel
    // For livedata
    binding.lifecycleOwner = this
    binding.viewmodel = viewModel
    binding.executePendingBindings()
    adapter = CatalogSpeechesAdapter()
    recyclerView = binding.rvSpeeches
    recyclerView.setHasFixedSize(true)
    recyclerView.adapter = adapter
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }


  private inner class CatalogSpeechesAdapter : PagingDataAdapter<CommonModel.Speech, CatalogSpeechesAdapter.CatalogSpeechesViewHolder>(DiffUtilCallback()) {

    private inner class CatalogSpeechesViewHolder(private val binding: SpeechesViewHolderBinding) : RecyclerView.ViewHolder(binding.root) {
      fun bind(speech: CommonModel.Speech) {
	binding.speech = speech
	binding.executePendingBindings()
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogSpeechesViewHolder {
      val binding = SpeechesViewHolderBinding.inflate(layoutInflater, parent, false)
      return CatalogSpeechesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatalogSpeechesViewHolder, position: Int) {
      val currentItem = getItem(position)
      if (currentItem != null) {
	holder.bind(currentItem)
      }
    }
  }

  private inner class DiffUtilCallback : DiffUtil.ItemCallback<CommonModel.Speech>() {
    override fun areItemsTheSame(oldItem: CommonModel.Speech, newItem: CommonModel.Speech): Boolean {
      return oldItem.SpeechId == newItem.SpeechId
    }

    override fun areContentsTheSame(oldItem: CommonModel.Speech, newItem: CommonModel.Speech): Boolean {
      return oldItem.Theme == newItem.Theme
    }

  }

}