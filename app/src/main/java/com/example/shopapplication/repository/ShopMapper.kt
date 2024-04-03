package com.example.shopapplication.repository

import com.example.shopapplication.content.ShopViewType

object ShopMapper {

    fun mapTimeStamps(videoReferenceId: String?, timestamps: List<Int?>?) = Result.runCatching {
        Pair(videoReferenceId, mutableListOf<Int>().apply {
            timestamps?.forEach {
                it?.let {
                    add(it)
                }
            }
        })
    }

    fun map(
        list: List<GetAdsQuery.BingAd?>?,
        cellType: CellType,
        railType: RailType,
        shopViewType: ShopViewType,
        favoriteList: MutableList<String>,
    ) = Result.runCatching {
        ShopRailItem(
            list = list,
            cellType = cellType,
            railType = railType,
            shopViewType = shopViewType,
            favoriteList = favoriteList,
        )
    }

    class ShopRailItem(
        private val list: List<GetAdsQuery.BingAd?>?,
        override val cellType: CellType,
        override val railType: RailType,
        private val shopViewType: ShopViewType,
        private val favoriteList: MutableList<String>,
    ) : RailItem {

        override val id: ContentId
            get() = "Shop a look from the video".toContentId()

        override val title: RailTitle
            get() = RailTitle(translationKey = SHOP_THE_LOOK_TRANSLATION_TEXT, fallback = SHOP_THE_LOOK_FALLBACK)


        override val assetType: AssetType
            get() = AssetType.SHOP_ITEM

        override val cells: List<CellItem>
            get() = list?.distinctBy { it?.id }?.map {
                ShopCellItem(it, shopViewType, favoriteList)
            }.orEmpty()

        override val displayLocale: Locale
            get() = throw UnsupportedOperationException("Not applicable for shop")
    }


    class ShopCellItem(
        private val bingAd: GetAdsQuery.BingAd?,
        private val shopViewType: ShopViewType,
        private var favoriteList : MutableList<String>
    ) : CellItem {

        override val id: ContentId
            get() =  bingAd?.id?.toContentId() ?: Empty

        override val showId: ContentId? get() = null

        override val ageRating: String get() = String.empty

        override val webUrl: String get() = throw UnsupportedOperationException("Not applicable for Shop")

        override val title: String
            get() = bingAd?.seller?.name.orEmpty()

        override val originalTitle: String get() = String.empty

        override val description: String
            get() = bingAd?.itemOffered?.description.orEmpty()

        override val episodeNumber: Int
            get() = throw UnsupportedOperationException("Not applicable for Shop")

        override val releaseDate: LocalDate?
            get() = null

        override val impressionToken: String?
            get() = bingAd?.impressionToken

        override val pingUrlBase: String?
            get() = bingAd?.pingUrlBase

        override val visibilityFeedbackUrl: String?
            get() = bingAd?.visibilityFeedbackUrl

        override val pageLoadPingUrl: String?
            get() = bingAd?.pageLoadPingUrl

        override val urlPingSuffix: String?
            get() = bingAd?.urlPingSuffix

        override val assetType: AssetType
            get() = AssetType.SHOP_ITEM

        override val businessType: String
            get() = String.empty

        override val slug: String
            get() = String.empty

        override val genres: List<String>
            get() = emptyList()

        override fun getDuration(): Int = 0

        override val additionalInfo: AdditionalCellInfo
            get() = ShopAdditionalInfo(
                buyAt = BUY_ON.plus(bingAd?.seller?.name.orEmpty()),
                buyAtUrl = bingAd?.url.orEmpty(),
                price = bingAd?.price.orEmpty(),
                imageURL = bingAd?.itemOffered?.contentImageUrl.orEmpty(),
                itemName = bingAd?.seller?.name.orEmpty(),
                itemDesc = bingAd?.itemOffered?.name.orEmpty(),
                discountedPrice = bingAd?.discountPrice.orEmpty(),
                currencySymbol = bingAd?.currencySymbol.orEmpty(),
                shopViewType = shopViewType,
            )

        override val assetTypeInt: Int
            get() = throw UnsupportedOperationException("Unsupported property asset type")

        override val displayLocale: Locale
            get() = throw UnsupportedOperationException("Unsupported property display locale")

        override val type: Content.Type by lazy(LazyThreadSafetyMode.NONE) {
            Content.Type.NA
        }

        override val season: Season?
            get() = null

        override fun getImageUrl(width: Int, height: Int, scaleFactor: Float) =
            throw UnsupportedOperationException("Not applicable for Shop")

        override val durationMinsAndSecs: String
            get() = String.empty

        override var isFavorite: Boolean
            get() = favoriteList.contains(id.value)
            set(value) {
                if (value) {
                    if (id.value !in favoriteList) {
                        favoriteList.add(id.value)
                    }
                } else {
                    favoriteList.remove(id.value)
                }
            }
    }
}