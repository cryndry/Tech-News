package com.technews.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NewsViewModel: ViewModel() {
    private var pageCount = 1
    private val _fetchStatus = mutableStateOf(FetchStatus())
    val fetchStatus: State<FetchStatus> = _fetchStatus

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
}

data class FetchStatus(
    var loading: Boolean = true,
    var news: List<News> = emptyList(),
    var error: String? = null,
)

data class News(
    val title: String,
    val url: String,
    val image: String,
)