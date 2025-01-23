package com.technews.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.technews.data.NewsViewModel

@Composable
fun NewsScreen(modifier: Modifier = Modifier) {
    val newsViewModel = NewsViewModel()
    val newsFetchState = newsViewModel.fetchStatus.value

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when {
                newsFetchState.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                newsFetchState.error != null -> {
                    Text(newsFetchState.error!!)
                }
                else -> {
                    println(newsFetchState.news)
                    LazyColumn() {
                        itemsIndexed(newsFetchState.news) { index, newsItem ->
                            Text(newsItem.title)
                        }
                    }
                }
            }
        }
    }
}
