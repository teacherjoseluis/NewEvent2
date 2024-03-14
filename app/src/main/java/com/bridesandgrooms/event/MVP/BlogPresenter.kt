package com.bridesandgrooms.event.MVP

import android.content.Context
import android.view.View
import com.bridesandgrooms.event.DashboardBlog
import com.bridesandgrooms.event.Functions.BlogPost
import com.bridesandgrooms.event.Functions.FirebaseGetBlogSuccess
import com.bridesandgrooms.event.Functions.getBlog

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
            override fun onGetBlogSuccess(bloglist: java.util.ArrayList<BlogPost>) {
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
        fun onViewBlogSuccess(inflatedView: View, bloglist: ArrayList<BlogPost>)
        fun onViewBlogError(errcode: String)
    }
}