package com.bridesandgrooms.event.Functions

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

fun getUserCountry(context: Context): String {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    // Try to get country from SIM card
    val simCountry = telephonyManager.simCountryIso?.uppercase()

    // If SIM country is null or empty, fallback to system locale
    return simCountry?.takeIf { it.isNotEmpty() } ?: Locale.getDefault().country
}