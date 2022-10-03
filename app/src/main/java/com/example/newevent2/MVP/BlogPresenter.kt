package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.DashboardBlog
import com.example.newevent2.Functions.Blog
import com.example.newevent2.Functions.FirebaseGetBlogSuccess
import com.example.newevent2.Functions.getBlog

class BlogPresenter(
    var context: Context,
    var fragment: DashboardBlog,
    var inflatedView: View,
    var language: String
) {
    init {
        language = when (language) {
            "en" -> "en"
            else -> "es"
        }
        getBlog(language, object : FirebaseGetBlogSuccess {
            override fun onGetBlogSuccess(bloglist: java.util.ArrayList<Blog>) {
                if (bloglist.isNotEmpty()) {
                    //Blog has elements to be shown
                    fragment.onViewBlogSuccess(inflatedView, bloglist)
                } else {
                    //No Blog entries for the language
                    fragment.onViewBlogError("EMPTY_BLOG")
                }
            }
        })
    }

    interface ViewBlogActivity {
        fun onViewBlogSuccess(inflatedView: View, bloglist: ArrayList<Blog>)
        fun onViewBlogError(errcode: String)
    }
}