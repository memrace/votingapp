package com.northis.votingapp.catalog.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.northis.votingapp.app.CommonModel
import com.northis.votingapp.catalog.CatalogModel
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 0

class SpeechesDataSource(private val catalogModel: CatalogModel, private val query: CatalogModel.SpeechQuery) : PagingSource<Int, CommonModel.Speech>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommonModel.Speech> {
    return try {
      val skip = params.key ?: STARTING_PAGE_INDEX
      val take = params.loadSize
      val response = catalogModel.getSpeeches(query = query, skip = skip, take = take)
      val speeches = response.body()?.Speeches ?: listOf()
      LoadResult.Page(
        data = speeches,
        prevKey = if (skip == 0) null else skip - take,
        nextKey = if (speeches.isEmpty()) null else skip + take
      )
    } catch (exception: IOException) {
      LoadResult.Error(exception)
    } catch (exception: HttpException) {
      LoadResult.Error(exception)
    }

  }

  override fun getRefreshKey(state: PagingState<Int, CommonModel.Speech>): Int? {
    return null
  }


}