package com.technews.screens

import com.technews.data.News
import com.technews.data.NewsViewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.technews.LocalNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter


@Composable
fun NewsScreen(newsViewModel: NewsViewModel) {
    val newsFetchState = newsViewModel.fetchStatus.value
    val navController = LocalNavController.current

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
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    ) {
                        itemsIndexed(newsFetchState.news) { index, newsItem ->
                            NewsScreenItem(newsItem) {
                                newsViewModel.viewModelScope.launch {
                                    withContext(Dispatchers.IO) {
                                        newsViewModel.getNewsDetail(newsItem.url)
                                        withContext(Dispatchers.Main) {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("news", newsItem)
                                            navController.navigate(ScreenManager.NewsDetailScreen.route)
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            BottomLoadingIndicator {
                                newsViewModel.getNextPage()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsScreenItem(newsItem: News, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
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
            Text(
                newsItem.title,
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.W400,
                ),
            )
            Spacer(Modifier.height(16.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val secondaryTextStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    color = Color.Black.copy(alpha = 0.6f),
                )

                Text(
                    newsItem.date.format(DateTimeFormatter.ofPattern("dd MM yyyy")),
                    style = secondaryTextStyle,
                )
                Text(
                    newsItem.source,
                    style = secondaryTextStyle,
                )
            }
        }
    }
}

@Composable
fun BottomLoadingIndicator(callback: () -> Unit) {
    Row (
        modifier = Modifier.height(40.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }

    LaunchedEffect(null) {
        callback()
    }
}
