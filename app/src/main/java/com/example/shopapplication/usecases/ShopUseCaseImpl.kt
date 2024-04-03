package com.example.shopapplication.usecases

import com.example.shopapplication.content.ShopRequest
import com.example.shopapplication.repository.ShopRepository


class ShopUseCaseImpl(private val shopRepository: ShopRepository) : ShopUseCase {

    override suspend fun execute(input: ShopUseCase.Input): Result<ShopUseCase.Output> =
        Result.runCatching {
            when (input.operationType) {
                ShopUseCase.OperationType.GET_SHOP_ITEMS -> {
                    val data = shopRepository.getShopItems(
                        shopRequest = ShopRequest(
                            shopViewType = input.shopViewType,
                            timeStamp = input.timeStamp.toString(),
                            videoRefId = input.videoRefId
                        )
                    ).getOrThrow()
                    ShopUseCase.Output.ShopItems(data)
                }

                ShopUseCase.OperationType.GET_SHOP_TIMESTAMPS -> {
                    val videoRefTimestampPair = shopRepository.getShopTimeStamps(assetId = input.assetId).getOrThrow()
                    ShopUseCase.Output.ShopTimeStamps(videoRefTimestampPair.first,videoRefTimestampPair.second)
                }

                ShopUseCase.OperationType.GET_WISHLISTED_ITEMS -> ShopUseCase.Output.WishlistedItems(
                    shopRepository.getWishlistedItems().getOrThrow()
                )

                ShopUseCase.OperationType.CLEAR_DATA -> {
                    shopRepository.clearData()
                    ShopUseCase.Output.Empty
                }

                ShopUseCase.OperationType.GET_CACHED_TIMESTAMPS ->
                    ShopUseCase.Output.CachedTimeStamps(shopRepository.getCachedTimestamp())


                ShopUseCase.OperationType.GET_WISHLIST_COUNT -> ShopUseCase.Output.WishlistCount(
                    shopRepository.getWishlistCount().getOrThrow()
                )

                ShopUseCase.OperationType.SEND_IMPRESSION ->{
                    shopRepository.sendImpression(impressionUrl = input.url )
                    ShopUseCase.Output.Empty
                }
            }
        }

}