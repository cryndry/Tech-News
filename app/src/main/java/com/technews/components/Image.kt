package com.technews.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlin.math.min

@Composable
fun DynamicHeightImage(url: String, maxHeight: Int? = null) {
    var aspectRatio by remember { mutableStateOf(1f) }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .size(Size.ORIGINAL)
            .build(),
        onSuccess = { result ->
            val width = result.result.drawable.intrinsicWidth
            val height = result.result.drawable.intrinsicHeight
            aspectRatio = if (width > 0) width.toFloat() / min(height, maxHeight ?: height) else 1f
        }
    )

    Box(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .heightIn(max = maxHeight?.dp ?: Dp.Unspecified)
            .fillMaxWidth()
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
    }
}