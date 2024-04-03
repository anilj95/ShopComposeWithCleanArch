package com.example.shopapplication.state

sealed interface ShopControlState {
    object Close : ShopControlState

    data class ShowToast(val message : String, val show : Boolean) : ShopControlState

    object UpdateTitle : ShopControlState
    data class UpdateItemCount(val count: Int) : ShopControlState

    object CloseWishlistAndOpenShop : ShopControlState

    data class EmptyWishlistView(val isEnabled : Boolean) : ShopControlState
    data class EmptyShopListView(val isEnabled : Boolean) : ShopControlState

    data class ShopBannerVisibility(val isVisible : Boolean) : ShopControlState

    data class ShopImpression(val shopImpressionType: ShopImpressionType, val shopImpressionData: ShopImpressionData) : ShopControlState

    object DisableWishlist : ShopControlState
    object ToastShown : ShopControlState

    data class ShopIconVisibility(
        val showTooltip: Boolean,
        val showIcon: Boolean,
        val tooltipText: String
    ) : ShopControlState
}