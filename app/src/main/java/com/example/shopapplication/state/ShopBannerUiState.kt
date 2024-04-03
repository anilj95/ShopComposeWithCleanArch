package com.example.shopapplication.state

import com.example.shopapplication.utils.empty


data class ShopBannerUiState(
    val enableWishlist: Boolean = false,
    val wishlistCountText: String = String.empty,
    val bannerText : String = String.empty
)
