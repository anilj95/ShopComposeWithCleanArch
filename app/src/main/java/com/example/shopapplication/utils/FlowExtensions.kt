package com.example.shopapplication.utils

import kotlinx.coroutines.flow.MutableStateFlow

// TODO Move to more suitable place in the future
inline fun <T> MutableStateFlow<T>.updateValue(block: T.() -> T) {
    value = value.block()
}
