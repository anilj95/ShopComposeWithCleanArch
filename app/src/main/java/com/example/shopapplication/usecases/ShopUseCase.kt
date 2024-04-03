package com.example.shopapplication.usecases

import com.example.shopapplication.content.ShopViewType
import com.example.shopapplication.utils.empty


interface ShopUseCase : SuspendingUseCase<ShopUseCase.Input, Result<ShopUseCase.Output>> {
    data class Input(
        val shopViewType: ShopViewType = ShopViewType.PORTRAIT,
        val operationType: OperationType = OperationType.GET_SHOP_ITEMS,
        val pair : Pair<List<Int>,Int> = Pair(emptyList(), 0),
        val timeStamp : Int = 0,
        val assetId : String = String.empty,
        val videoRefId : String = String.empty,
        val url : String = String.empty
    )

    enum class OperationType {
        GET_SHOP_ITEMS,
        GET_SHOP_TIMESTAMPS,
        GET_WISHLISTED_ITEMS,
        CLEAR_DATA,
        GET_CACHED_TIMESTAMPS,
        GET_WISHLIST_COUNT,
        SEND_IMPRESSION
    }

    sealed class Output {

        object Empty : Output()
        data class ShopItems(val railItem: RailItem) : Output()
        data class ShopTimeStamps(val videoReferenceId: String?, val timeStamps: List<Int>) :
            Output()
        data class WishlistedItems(val railItem: List<RailItem>) : Output()
        data class CachedTimeStamps(val timeStamps: List<String>) : Output()
        data class WishlistCount(val count : Int) : Output()
    }
}