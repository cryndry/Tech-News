package com.technews.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.technews.components.DynamicHeightImage

import com.technews.data.News
import com.technews.data.NewsViewModel


@Composable
fun NewsDetailScreen(newsItem: News, newsViewModel: NewsViewModel) {
    val newsDetailsState = newsViewModel.newsItemDetails.value
    val scrollState = rememberScrollState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(40.dp, 40.dp))
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(50.dp, 50.dp),
                        ambientColor = Color.Black.copy(alpha = 0.14.toFloat())

                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .verticalScroll(scrollState)
                ) {
                    DynamicHeightImage(newsItem.image, maxHeight = 200)
                    Text(
                        newsItem.title,
                        modifier = Modifier.padding(vertical = 16.dp),
                        fontSize = 20.sp
                    )

                    newsDetailsState.map { newsDetailsItem ->
                        if (newsDetailsItem.content.isNotBlank())
                            when (newsDetailsItem.tag) {
                                "img" -> {
                                    DynamicHeightImage(newsDetailsItem.content)
                                }

                                "p" -> {
                                    Text(
                                        newsDetailsItem.content,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        fontSize = 16.sp,
                                    )
                                }
                            }
                    }
                }
            }
        }
    }
}