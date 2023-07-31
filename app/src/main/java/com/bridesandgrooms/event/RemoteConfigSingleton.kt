package com.bridesandgrooms.event

object RemoteConfigSingleton {
    private var autocreateTaskPayment_Feature = true
    private var enable_foryoutab = false
    private var category_layout = "card"

    fun getautocreateTaskPayment(): Boolean {
        return autocreateTaskPayment_Feature
    }

    // Setter method
    fun setautocreateTaskPayment(featureflag: Boolean) {
        autocreateTaskPayment_Feature = featureflag
    }

    fun get_enable_foryoutab(): Boolean {
        return enable_foryoutab
    }

    // Setter method
    fun set_enable_foryoutab(enable_foryoutab: Boolean) {
        this.enable_foryoutab = enable_foryoutab
    }
    fun get_category_layout(): String {
        return category_layout
    }

    // Setter method
    fun set_category_layout(category_layout: String) {
        this.category_layout = category_layout
    }

}