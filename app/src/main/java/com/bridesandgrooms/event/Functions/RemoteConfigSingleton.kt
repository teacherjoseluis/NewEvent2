package com.bridesandgrooms.event.Functions

object RemoteConfigSingleton {
    private var autocreateTaskPayment_Feature:Boolean = true
    private var enable_foryoutab = false
    private var category_layout = "card"
    private var developer_mail = false
    private var video_login = true
    private var showads = false
    private var reviewbox = false

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
        RemoteConfigSingleton.enable_foryoutab = enable_foryoutab
    }
    fun get_category_layout(): String {
        return category_layout
    }

    // Setter method
    fun set_category_layout(category_layout: String) {
        RemoteConfigSingleton.category_layout = category_layout
    }

    fun get_video_login(): Boolean {
        return video_login
    }

    // Setter method
    fun set_video_login(video_login: Boolean) {
        RemoteConfigSingleton.video_login = video_login
    }

    fun get_developer_mail(): Boolean {
        return developer_mail
    }

    // Setter method
    fun set_developer_mail(developer_mail: Boolean) {
        RemoteConfigSingleton.developer_mail = developer_mail
    }

    fun get_showads(): Boolean {
        return showads
    }

    // Setter method
    fun set_showads(showads: Boolean) {
        RemoteConfigSingleton.showads = showads
    }

    fun get_reviewbox(): Boolean {
        return reviewbox
    }
    // Setter method
    fun set_reviewbox(reviewbox: Boolean) {
        RemoteConfigSingleton.reviewbox = reviewbox
    }
}