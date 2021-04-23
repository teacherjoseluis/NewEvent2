package com.example.newevent2.MVP

import com.example.newevent2.DashboardBlog
import com.example.newevent2.Functions.*
import com.example.newevent2.DashboardView

class BlogPresenter(
    view: DashboardBlog,
    language: String
) {
    var viewDashboardBlog: DashboardBlog = view

    init {
        getBlog(language, object : FirebaseGetBlogSuccess {
            override fun onGetBlogSuccess(bloglist: java.util.ArrayList<Blog>) {
                if (bloglist.isNotEmpty()) {
                    //Blog has elements to be shown
                    viewDashboardBlog.onViewBlogSuccess(bloglist)
                } else {
                    //No Blog entries for the language
                    viewDashboardBlog.onViewBlogError("EMPTY_BLOG")
                }
            }
        })
    }

    interface ViewBlogActivity {
        fun onViewBlogSuccess(bloglist: ArrayList<Blog>)
        fun onViewBlogError(errcode: String)
    }
}