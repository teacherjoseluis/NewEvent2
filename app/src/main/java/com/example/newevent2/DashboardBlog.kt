package com.example.newevent2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.Blog
import com.example.newevent2.Functions.Loginfo
import com.example.newevent2.MVP.BlogPresenter
import com.example.newevent2.MVP.LogPresenter
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.view.*
import kotlinx.android.synthetic.main.dashboardblog.view.*
import kotlinx.android.synthetic.main.welcome.*
import kotlinx.android.synthetic.main.welcome.recentactivityrv

class DashboardBlog() : Fragment(), BlogPresenter.ViewBlogActivity {

    lateinit var recyclerViewBlog: RecyclerView
    private lateinit var presenter: BlogPresenter

    var language = "en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        language = arguments!!.get("language").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.dashboardblog, container, false)

        recyclerViewBlog = inf.blogrv
        recyclerViewBlog.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        presenter = BlogPresenter(context!!, this, inf, language)
        return inf
    }
    override fun onViewBlogSuccess(inflatedView: View, bloglist: ArrayList<Blog>) {
        //Consider adding a try catch in case there is no data coming from Firebase
        inflatedView.emptyrecyclerview.visibility = View.GONE
        val rvAdapter = Rv_BlogAdapter(bloglist)
        blogrv.adapter = rvAdapter
    }

    override fun onViewBlogError(errcode: String) {
        blogrv.visibility = View.GONE
    }
}