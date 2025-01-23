package com.technews.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class NewsViewModel: ViewModel() {
    private var pageCount = 1
    private val _fetchStatus = mutableStateOf(FetchStatus())
    val fetchStatus: State<FetchStatus> = _fetchStatus
    private val newsService = NewsService()

    init {
        getNextPage()
    }

    fun getNextPage() {
        viewModelScope.launch {
            try {
                _fetchStatus.value.loading = true
                _fetchStatus.value.error = null

                val fetchedNews = newsService.getNews(pageCount)

                _fetchStatus.value.loading = false
                _fetchStatus.value.news = fetchedNews

                pageCount++
            } catch (e: Exception) {
                _fetchStatus.value.loading = false
                _fetchStatus.value.error = e.message
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