package com.example.shopapplication.state

import com.example.shopapplication.content.ShopScreenInfo
import com.example.shopapplication.utils.empty

data class ShopUiState(
    val title: String = String.empty,
    val durationInfo: String = String.empty,
    val showLoader: Boolean = false,
    val showToast: Boolean = false,
    val toastMessage : String = String.empty,
    val enableEmptyWishlist : Boolean = false,
    val enableEmptyShopScreen : Boolean = false,
    val onScreenItemSize : Int = 0,
    val shopScreenInfo: ShopScreenInfo = ShopScreenInfo.Default,
)
