package com.bridesandgrooms.event

object AdManagerSingleton {
    private val adManager = AdManager(adDelayTimeMillis = 180000) // Instantiate AdManager with a 60-second delay time

    fun getAdManager(): AdManager {
        return adManager
    }
}