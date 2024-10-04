package com.bridesandgrooms.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.Firebase.BlogPost
import com.bridesandgrooms.event.MVP.BlogPresenter
import com.bridesandgrooms.event.databinding.DashboardblogBinding

class DashboardBlog : Fragment(), BlogPresenter.ViewBlogActivity {

    private lateinit var recyclerViewBlog: RecyclerView
    private lateinit var presenter: BlogPresenter
    private lateinit var inf: DashboardblogBinding

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
        inf = DataBindingUtil.inflate(inflater, R.layout.dashboardblog, container, false)

        recyclerViewBlog = inf.blogrv
        recyclerViewBlog.apply {
            layoutManager = LinearLayoutManager(inf.root.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        return inf.root
    }

    override fun onResume() {
        super.onResume()
        try {
            presenter = BlogPresenter(requireContext(), this, inf.root, language)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun onViewBlogSuccess(inflatedView: View, bloglist: ArrayList<BlogPost>) {
        //Consider adding a try catch in case there is no data coming from Firebase
        inf.emptyrecyclerview.visibility = View.GONE
        val rvAdapter = Rv_BlogAdapter(bloglist)
        inf.blogrv.adapter = rvAdapter
    }

    override fun onViewBlogError(errcode: String) {
        inf.blogrv.visibility = View.GONE
    }
}