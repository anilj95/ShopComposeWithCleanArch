package com.example.shopapplication.repository

import com.apollographql.apollo3.ApolloClient
import com.example.shopapplication.content.ShopRequest
import com.example.shopapplication.service.BingApiService
import com.example.shopapplication.service.GraphQLHeadersRepository


class ShopRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val bingApiService: BingApiService,
    private val graphQLHeadersRepository: GraphQLHeadersRepository,
) : ShopRepository {

    private val favoriteList = mutableListOf<String>()
    private var memoryCache: MutableMap<String, List<GetAdsQuery.BingAd?>?> = linkedMapOf()

    override suspend fun getShopItems(shopRequest: ShopRequest): Result<RailItem> {
        return when (memoryCache.containsKey(shopRequest.timeStamp) || shopRequest.timeStamp.equals(
            ZERO.toString(), true)) {
            true -> handleCachedData(shopRequest = shopRequest)
            else -> {
                val response = apolloClient.queryToNetworkResponse(
                    query = GetAdsQuery(
                        videoReferenceId = shopRequest.videoRefId,
                        timeStamp = shopRequest.timeStamp
                    ),
                    headers = graphQLHeadersRepository.withHeaders(xAccessToken = true, userAgent = true),
                )
                response.fold(onSuccess = { networkResponse ->
                    val ads = networkResponse.getOrThrow().getAds?.bingAds
                    return when (networkResponse) {
                        is NetworkResponse.Failure.GraphQL -> handleGQLHttpFailure(errorList = networkResponse.errors, shopViewType = shopRequest.shopViewType)
                        else -> ShopMapper.map(
                            list = getAllList(ads),
                            cellType = CellType.SHOP_CELL,
                            railType = RailType.VERTICAL_LINEAR,
                            shopViewType = shopRequest.shopViewType,
                            favoriteList = favoriteList,
                        ).also {
                            if(ads.isNullOrEmpty().not()){
                                memoryCache[shopRequest.timeStamp] = ads
                            }
                        }
                    }
                }, onFailure = { Result.failure(it) })
            }
        }
    }
// for getting list
    private fun getAllList(networkList : List<GetAdsQuery.BingAd?>? = null) : List<GetAdsQuery.BingAd?> {
        val list = mutableListOf<GetAdsQuery.BingAd?>()
        networkList?.let { list.addAll(it) }
        memoryCache.entries.reversed().map { map -> map.value?.let { list.addAll(it) } }
        return list
    }

    private fun handleCachedData(shopRequest: ShopRequest) = ShopMapper.map(
        cellType = CellType.SHOP_CELL,
        railType = RailType.VERTICAL_LINEAR,
        shopViewType = shopRequest.shopViewType,
        favoriteList = favoriteList,
        list = getAllList()
    )


    private fun handleGQLHttpFailure(
        errorList: List<GraphQLError>,
        shopViewType: ShopViewType
    ) = when (memoryCache.isNotEmpty()) {
        true -> ShopMapper.map(
            list = getAllList(),
            cellType = CellType.SHOP_CELL,
            railType = RailType.VERTICAL_LINEAR,
            shopViewType = shopViewType,
            favoriteList = favoriteList,
        )

        else -> {
            val firstError = errorList.first()
            Result.failure(Throwable(firstError.internalError?.message ?: firstError.message))
        }
    }



    override suspend fun getShopTimeStamps(assetId: String): Result<Pair<String?, List<Int>>> {
        return apolloClient.queryToResult(
            query = AdStacksQuery(assetId),
            headers = graphQLHeadersRepository.withHeaders(xAccessToken = true, userAgent = true)
        ).map {
            ShopMapper.mapTimeStamps(it.adStacks?.videoReferenceId,it.adStacks?.timeStamps).getOrThrow() }
    }

    override suspend fun getWishlistedItems(): Result<List<RailItem>> {
        return ShopWishlistMapper.map(getAllList(), favoriteList)
    }

    override suspend fun sendImpression(impressionUrl: String) {
        bingApiService.sendImpression(impressionUrl)
    }

    override suspend fun getWishlistCount(): Result<Int> = Result.runCatching {
        getAllList().count { favoriteList.contains(it?.id.orEmpty()) }
    }

    override suspend fun getCachedTimestamp()  = memoryCache.keys.toList()

    override suspend fun clearData() {
        memoryCache.clear()
        favoriteList.clear()
    }
}