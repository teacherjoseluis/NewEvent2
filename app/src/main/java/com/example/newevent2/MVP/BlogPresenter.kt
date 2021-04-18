package com.example.newevent2.MVP

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.Functions.*
import com.example.newevent2.Functions.getLog
import com.example.newevent2.Functions.removeLog
import com.example.newevent2.WelcomeView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BlogPresenter(
    view: WelcomeView,
    language: String
) {
    var viewWelcome: WelcomeView = view

    init {
        getBlog(language, object : FirebaseGetBlogSuccess {
            override fun onGetBlogSuccess(bloglist: java.util.ArrayList<Blog>) {
                if (bloglist.isNotEmpty()) {
                    //Blog has elements to be shown
                    viewWelcome.onViewBlogSuccess(bloglist)
                } else {
                    //No Blog entries for the language
                    viewWelcome.onViewBlogError("EMPTY_BLOG")
                }
            }
        })
    }

    interface ViewBlogActivity {
        fun onViewBlogSuccess(bloglist: ArrayList<Blog>)
        fun onViewBlogError(errcode: String)
    }
}