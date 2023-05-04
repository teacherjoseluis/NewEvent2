package com.example.newevent2

object AdManagerSingleton {
    private val adManager = AdManager(adDelayTimeMillis = 60000) // Instantiate AdManager with a 60-second delay time

    fun getAdManager(): AdManager {
        return adManager
    }
}