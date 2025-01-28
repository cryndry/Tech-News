package com.technews.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime


data class FetchStatus(
    var loading: Boolean = true,
    var news: List<News> = emptyList(),
    var error: String? = null,
)

@Parcelize
data class News(
    val title: String,
    val url: String,
    val image: String,
    val date: ZonedDateTime,
    val source: WebSource,
): Parcelable


data class NewsDetailItem(
    val tag: String,
    val content: String,
)
