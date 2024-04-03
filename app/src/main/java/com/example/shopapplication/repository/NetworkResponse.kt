package com.example.shopapplication.repository

import okhttp3.ResponseBody
typealias Headers = Map<String, List<String>>

sealed class NetworkResponse<out T> {
    data class Success<T>(
        val statusCode: Int,
        val headers: Headers,
        val value: T,
        val cacheProperties: CacheProperties? = null,
    ) : NetworkResponse<T>()

    sealed class Failure : NetworkResponse<Nothing>() {
        data class Http(
            val statusCode: Int,
            val headers: Headers,
            val rawBody: ResponseBody?,
            val url: String,
            val errorCode: String? = null
        ) : Failure()

        data class IO(
            val exception: NetworkIOException,
        ) : Failure()

        data class Unknown(
            val exception: Throwable,
        ) : Failure()

        data class GraphQL(
            val errors: List<GraphQLError>,
            val operationName: String
        ) : Failure()
    }
}

