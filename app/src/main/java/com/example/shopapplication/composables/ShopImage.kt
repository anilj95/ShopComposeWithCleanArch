package com.example.shopapplication.composables

import android.icu.number.Scale
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.shopapplication.R


@Composable
fun ShopImage(
    modifier: Modifier,
    scale: Scale = Scale.FIT,
    imageUrl: String,
    errorPlaceHolder: Int = R.drawable.ic_launcher_background,
) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(data = imageUrl)
            .apply(block = fun ImageRequest.Builder.() {
                error(drawableResId = errorPlaceHolder).placeholder(drawableResId = errorPlaceHolder)
                scale(scale)
            }).build()),
        contentDescription = null,
    )
}