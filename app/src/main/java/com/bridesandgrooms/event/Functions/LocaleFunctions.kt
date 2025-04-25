package com.bridesandgrooms.event.Functions

import android.content.Context
import android.content.res.Configuration
import java.util.*

fun getEnglishString(context: Context, resId: Int): String {
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale.ENGLISH)

    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.getString(resId)
}