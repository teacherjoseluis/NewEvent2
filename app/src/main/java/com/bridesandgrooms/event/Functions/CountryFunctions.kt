package com.bridesandgrooms.event.Functions

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

fun getUserCountry(context: Context): String {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    // 1. Try SIM country
    val simCountry = telephonyManager.simCountryIso?.uppercase()
    if (!simCountry.isNullOrEmpty()) return simCountry

    // 2. Try network country (e.g., cellular network)
    val networkCountry = telephonyManager.networkCountryIso?.uppercase()
    if (!networkCountry.isNullOrEmpty()) return networkCountry

    // 3. Fallback to locale
    return Locale.getDefault().country.uppercase()
}
