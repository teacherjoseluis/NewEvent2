package com.bridesandgrooms.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.Blog
import com.bridesandgrooms.event.MVP.BlogPresenter
import kotlinx.android.synthetic.main.dashboardblog.*
import kotlinx.android.synthetic.main.dashboardblog.view.*

class DashboardBlog : Fragment(), BlogPresenter.ViewBlogActivity {

    private lateinit var recyclerViewBlog: RecyclerView
    private lateinit var presenter: BlogPresenter
    private lateinit var inf: View

    var language = "en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        language = requireArguments().get("language").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        inf = inflater.inflate(R.layout.dashboardblog, container, false)

        recyclerViewBlog = inf.blogrv
        recyclerViewBlog.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        return inf
    }

    override fun onResume() {
        super.onResume()
        try {
            presenter = BlogPresenter(requireContext(), this, inf, language)
        } catch (e: Exception) {
            println(e.message)
        }
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