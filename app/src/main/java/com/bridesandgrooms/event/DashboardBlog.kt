package com.bridesandgrooms.event

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.Firebase.BlogPost
import com.bridesandgrooms.event.MVP.BlogPresenter
import com.bridesandgrooms.event.UI.Adapters.Rv_BlogAdapter
import com.bridesandgrooms.event.UI.Fragments.MyNotes.Companion.TAG
import com.bridesandgrooms.event.databinding.DashboardblogBinding
import com.google.android.material.appbar.MaterialToolbar

class DashboardBlog : Fragment(), BlogPresenter.ViewBlogActivity, BlogFragmentActionListener {

    private lateinit var recyclerViewBlog: RecyclerView
    private lateinit var presenterBlog: BlogPresenter
    private lateinit var rvAdapter: Rv_BlogAdapter
    private lateinit var inf: DashboardblogBinding

    private lateinit var toolbar: MaterialToolbar

    private var mContext: Context? = null

    var language = "en"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.blog)

        // Inflate the layout for this fragment
        inf = DataBindingUtil.inflate(inflater, R.layout.dashboardblog, container, false)
        language = requireArguments().get("language").toString()
        //It seems to be failing here
        //TODO - It seems to be failing here

        recyclerViewBlog = inf.blogrv
        recyclerViewBlog.apply {
            layoutManager = LinearLayoutManager(inf.root.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        try {
            presenterBlog = BlogPresenter(mContext!!, this, inf.root, language)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return inf.root
    }

    override fun onResume() {
        super.onResume()
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.blog)
    }

    override fun onViewBlogSuccess(inflatedView: View, bloglist: ArrayList<BlogPost>) {
        //Consider adding a try catch in case there is no data coming from Firebase
        inf.emptyrecyclerview.visibility = View.GONE
        try {
            rvAdapter = Rv_BlogAdapter(this@DashboardBlog, bloglist, mContext!!)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.message.toString())
        }
        inf.blogrv.adapter = null
        inf.blogrv.adapter = rvAdapter
    }

    override fun onViewBlogError(errcode: String) {
        inf.blogrv.visibility = View.GONE
    }

    override fun onBlogFragmentWithData(blog: BlogPost) {
        val fragment = BlogContent()
        val bundle = Bundle()
        bundle.putParcelable("blog", blog)
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(R.anim.fade_in,0)
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.commit()
    }
}

interface BlogFragmentActionListener {
    fun onBlogFragmentWithData(blog: BlogPost)
}