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
                    val fetchedNews = NewsService.getNewsWebtekno(pageCount)
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
                 _newsItemDetails.value = NewsService.getNewsDetailWebtekno(url)
            } catch (e: Exception) {
                _newsItemDetails.value = arrayOf()
            }
        }
    }
}

