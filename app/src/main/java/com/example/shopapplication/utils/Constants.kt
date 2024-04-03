package com.example.shopapplication.utils

object Constants {
    const val BULLET_SEPARATOR = " • "
    const val SPACED_BULLET_SEPARATOR = " $BULLET_SEPARATOR "
    const val WHITE_SPACE = " "
    const val BIRTHDAY_FORMAT = "dd/MM/yyyy"
    const val FALLBACK_DEVICE_AD_ID = "fallback_device_ad_id"
    const val INVALID_INDEX = -1
    val CONTEST_LEADER_BOARD_TOP_RANK_ORDER = listOf(2, 1, 3)
    internal const val PLATFORM_ANDROID = "android"
    const val True = "TRUE"
    const val False = "FALSE"
}

val String.Companion.empty get() = ""