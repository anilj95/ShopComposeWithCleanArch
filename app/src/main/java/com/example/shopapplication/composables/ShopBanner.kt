package com.example.shopapplication.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopapplication.R
import com.example.shopapplication.state.ShopBannerControlState
import com.example.shopapplication.state.ShopBannerUiState
import com.example.shopapplication.utils.SHOP_BANNER_GRADIENT_COLOR

@Composable
fun ShopBanner(shopBannerUiState: ShopBannerUiState, shopBannerControlState: (ShopBannerControlState) -> Unit) {
    val gradientColors = remember {
        listOf(
            SHOP_BANNER_GRADIENT_COLOR,
            SHOP_BANNER_GRADIENT_COLOR.copy(alpha = 0.9f),
            SHOP_BANNER_GRADIENT_COLOR.copy(alpha = 0f)
        )
    }
    val gradientBrush = remember { Brush.horizontalGradient(gradientColors) }
    Box (modifier = Modifier
        .height(140.dp)
        .padding(8.dp)){
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = SHOP_BANNER_URL)
                    .apply(block = fun ImageRequest.Builder.() {
                        error(R.drawable.ic_launcher_background)
                        placeholder(R.drawable.ic_launcher_foreground)
                        scale(Scale.FIT)
                    }).build(),
            ),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()

        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .border(width = 1.dp, color = SORT_COLOR, shape = RoundedCornerShape(1.dp))) {}
        when (shopBannerUiState.enableWishlist) {
            true -> WishlistView(
                shopBannerControlState = shopBannerControlState,
                wishlistButtonText = shopBannerUiState.wishlistCountText,
                bannerText = shopBannerUiState.bannerText
            )

            else -> ExploreView(shopBannerControlState = shopBannerControlState)
        }

    }
}

@Composable
private fun ExploreView(shopBannerControlState: (ShopBannerControlState) -> Unit) {
    val shopButtonText = SHOP_EXPLORE_LOOKS_TEXT
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        ShopText(
            modifier = Modifier
                .width(290.dp),
            text = EXPLORE_BANNER_TEXT,
            font = ShopFont.NotoSansRegular,
            size = 14.sp,
            maxLines = 2,
            fontWeight = FontWeight.W500,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        ShopOutlinedButton(
            onClick = { shopBannerControlState.invoke(ShopBannerControlState.Shop(shopButtonText = shopButtonText)) },
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = PODCAST_BUTTON_COLOR),
            contentPadding = PaddingValues(all = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                size = 20.dp
            )

            ShopText(
                text = SHOP_EXPLORE_LOOKS_TEXT,
                font = ShopFont.NotoSansRegular,
                size = 12.sp,
                fontWeight = FontWeight.W700,
                lineHeight = 18.sp
            )

        }
    }
}

@Composable
private fun WishlistView(
    shopBannerControlState: (ShopBannerControlState) -> Unit,
    wishlistButtonText: String,
    bannerText : String
) {
    val shopButtonText = SHOP_EXPLORE_LOOKS_TEXT
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {

        ShopText(
            text = BUY_ITEMS_WISHLIST_TEXT,
            size = 12.sp,
            fontWeight = FontWeight.W700,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        ShopText(
            text = bannerText,
            size = 11.sp,
            fontWeight = FontWeight.W500,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            ShopOutlinedButton(shape = RoundedCornerShape(4.dp),
                border = BorderStroke(width = 1.dp, color = Color.White),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                onClick = {
                    shopBannerControlState.invoke(ShopBannerControlState.Wishlist(wishlistButtonText))
                }) {
                ShopText(
                    text = wishlistButtonText,
                    size = 12.sp,
                    fontWeight = FontWeight.W700,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            ShopButton(onClick = {
                shopBannerControlState.invoke(ShopBannerControlState.Shop(shopButtonText = shopButtonText))
            },colors = ButtonDefaults.outlinedButtonColors(backgroundColor = PODCAST_BUTTON_COLOR)) {
                Icon(
                    image = R.drawable.ic_launcher_background,
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    size = 20.dp
                )
                ShopText(
                    text = SHOP_EXPLORE_LOOKS_TEXT,
                    font = ShopFont.NotoSansRegular,
                    size = 12.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 18.sp
                )
            }
        }

    }
}
