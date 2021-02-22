package com.northis.votingapp.catalog

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.northis.votingapp.app.CommonModel
import javax.inject.Inject

class CatalogViewModel(private val catalogModel: CatalogModel, private val commonModel: CommonModel) : ViewModel() {
  val loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  private val querySearch = MutableLiveData(QUERY_DEFAULT)

  companion object {
    private val QUERY_DEFAULT = CatalogModel.SpeechQuery(CatalogModel.SpeechQueries.DEFAULT, "")
  }

  val speeches = querySearch.switchMap { speechQuery ->
    catalogModel.loadSpeeches(speechQuery).cachedIn(viewModelScope)
  }

  fun searchSpeeches(newQuery: CatalogModel.SpeechQuery) {
    querySearch.value = newQuery
  }
}


class CatalogViewModelFactory @Inject constructor(
  application: Application,
  private val catalogModel: CatalogModel,
  private val commonModel: CommonModel
) : ViewModelProvider.AndroidViewModelFactory(application) {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return CatalogViewModel(catalogModel, commonModel) as T
  }
}