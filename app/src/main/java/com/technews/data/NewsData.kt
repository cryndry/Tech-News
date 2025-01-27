package com.technews.data

import android.os.Parcelable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize


class NewsViewModel: ViewModel() {
    private var pageCount = 1
    private val _fetchStatus = mutableStateOf(FetchStatus())
    val fetchStatus: State<FetchStatus> = _fetchStatus

    private val _newsItemDetails = mutableStateOf(arrayOf<NewsDetailItem>())
    val newsItemDetails: State<Array<NewsDetailItem>> = _newsItemDetails

    init {
        getNextPage()
    }

    fun getNextPage() {
        viewModelScope.launch {
            try {
                _fetchStatus.value = _fetchStatus.value.copy(
                    loading = true,
                    error = null,
                )

                withContext(Dispatchers.IO) {
                    val fetchedNews = NewsService.getNews(pageCount)
                    if (fetchedNews.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            _fetchStatus.value = _fetchStatus.value.copy(
                                loading = false,
                                news = _fetchStatus.value.news + fetchedNews,
                            )
                        }
                    }
                }

                pageCount++
            } catch (e: Exception) {
                _fetchStatus.value = _fetchStatus.value.copy(
                    loading = false,
                    error = e.message,
                )
            }
        }
    }

    fun getNewsDetail(url: String) {
        viewModelScope.launch {
            try {
                 _newsItemDetails.value = NewsService.getNewsDetail(url)
            } catch (e: Exception) {
                _newsItemDetails.value = arrayOf()
            }
        }
    }
}

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
): Parcelable


data class NewsDetailItem(
    val tag: String,
    val content: String,
)