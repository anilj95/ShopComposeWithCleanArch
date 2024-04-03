package com.example.shopapplication.state

sealed interface ShopWishlistState {
    data class ShowToast(val message: String) : ShopWishlistState
    data class UpdateCellAtPosition(val position : Int) : ShopWishlistState
    object EmptyWishlist : ShopWishlistState
}