package com.northis.votingapp.catalog

import com.northis.votingapp.app.CommonModel
import java.util.*

class CatalogModel {
  data class SpeechStatus(
    val InCalendar: String = "InCalendar",
    val InCatalog: String = "InCatalog",
    val InVoting: String = "InVoting"
  )

  data class Speeches(
    val Speeches: ArrayList<CommonModel.Speech>,
    val SpeechesCount: Int
  )
}