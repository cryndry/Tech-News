package com.technews.screens

import com.technews.data.News
import com.technews.data.NewsViewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun NewsScreen(newsViewModel: NewsViewModel, modifier: Modifier = Modifier) {
    val newsFetchState = newsViewModel.fetchStatus.value
    val lazyListState = rememberLazyListState()

    Scaffold { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            when {
                newsFetchState.loading && newsFetchState.news.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                newsFetchState.error != null -> {
                    Text(newsFetchState.error!!)
                }
                else -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    ) {
                        itemsIndexed(newsFetchState.news) { index, newsItem ->
                            NewsScreenItem(newsItem)
                        }
                        item() {
                            BottomLoadingIndicator(lazyListState, {newsViewModel.getNextPage()})
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsScreenItem(newsItem: News) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(newsItem.image),
                contentDescription = null,
                modifier = Modifier.height(200.dp).fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
            Spacer(Modifier.height(8.dp))
            Text(newsItem.title)
        }
    }
}

@Composable
fun BottomLoadingIndicator(lazyListState: LazyListState, callback: () -> Unit) {
    Row (
        modifier = Modifier.height(40.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }

    LaunchedEffect(lazyListState) {
        val layoutInfo = lazyListState.layoutInfo
        val totalItems = layoutInfo.totalItemsCount
        val visibleItems = layoutInfo.visibleItemsInfo
        val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0

        println(totalItems)
        println(visibleItems)
        println(lastVisibleItemIndex)

        if (totalItems > 0 && lastVisibleItemIndex >= totalItems - 5) {
            callback()
        }
    }
}