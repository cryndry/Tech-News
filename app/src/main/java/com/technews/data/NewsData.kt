package com.technews.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date


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
    val date: Date? = null,
    val source: String? = null,
): Parcelable


data class NewsDetailItem(
    val tag: String,
    val content: String,
)
