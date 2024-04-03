package com.example.shopapplication.state

sealed interface ShopBannerControlState {
    data class Wishlist(val wishlistButtonText : String) : ShopBannerControlState
    data class Shop(val shopButtonText : String) : ShopBannerControlState
}