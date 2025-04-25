package com.bridesandgrooms.event.MVP

import android.content.Context
import android.view.View
import com.bridesandgrooms.event.UI.Fragments.DashboardBlog
import com.bridesandgrooms.event.Functions.Firebase.BlogPost
import com.bridesandgrooms.event.Functions.Firebase.FirebaseGetBlogSuccess
import com.bridesandgrooms.event.Functions.Firebase.getBlog
import com.bridesandgrooms.event.Functions.UserSessionHelper
import com.bridesandgrooms.event.Model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlogPresenter(
    var context: Context,
    var fragment: DashboardBlog,
    var inflatedView: View
) {
    private lateinit var user: User

    init {
        CoroutineScope(Dispatchers.Main).launch {
            user = User.getUserAsync() // suspend function assumed
            val language = when (user.language) {
                "English" -> "en"
                "Español" -> "es"
                "Portugues" -> "pt"
                "Français" -> "fr"
                else -> "en"
            }
            getBlog(language, object : FirebaseGetBlogSuccess {
                override fun onGetBlogSuccess(bloglist: ArrayList<BlogPost>) {
                    if (bloglist.isNotEmpty()) {
                        fragment.onViewBlogSuccess(inflatedView, bloglist)
                    } else {
                        fragment.onViewBlogError("EMPTY_BLOG")
                    }
                }
            })
        }
    }

    interface ViewBlogActivity {
        fun onViewBlogSuccess(inflatedView: View, bloglist: ArrayList<BlogPost>)
        fun onViewBlogError(errcode: String)
    }
}

