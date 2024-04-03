package com.example.shopapplication.service

interface GraphQLHeadersRepository {
    suspend fun withHeaders(
        guestToken: Boolean = false,
        xAccessToken: Boolean = false,
        accessTokenWithBearer: Boolean = false,
        accessTokenWithoutBearer: Boolean = false,
        contentTypeApplicationJson: Boolean = false,
        acceptTextPlain: Boolean = false,
        cacheControlNoCache: Boolean = false,
        profileIdForWatchHistoryGuest : String? = null,
        eTag: String? = null,
        deviceId: Boolean = false,
        esk: Boolean = false,
        guestUserTemporaryLoginHeaderName: Boolean = false,
        userAgent : Boolean = false,
        xUserType: String? = null
    ): Map<String, String>
}