package com.example.shopapplication.content

data class ShopAdditionalInfo(
    val buyAt: String,
    val buyAtUrl: String,
    val price: String,
    val imageURL: String,
    val itemName: String,
    val itemDesc: String,
    val discountedPrice: String,
    val currencySymbol: String,
    val shopViewType : ShopViewType,
) : AdditionalCellInfo

