package com.example.shopapplication.repository

import com.example.shopapplication.content.ShopRequest


interface ShopRepository {
    suspend fun getShopItems(shopRequest: ShopRequest): Result<RailItem>

    suspend fun getShopTimeStamps(assetId : String): Result<Pair<String?,List<Int>>>

    suspend fun getWishlistedItems(): Result<List<RailItem>>

    suspend fun getWishlistCount(): Result<Int>

    suspend fun sendImpression(impressionUrl: String)

    suspend fun getCachedTimestamp() : List<String>

    suspend fun clearData()
}