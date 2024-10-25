package com.bridesandgrooms.event

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Functions.Firebase.BlogPost
import com.bridesandgrooms.event.Functions.convertToBlogStringDate
import Application.MyFirebaseApp
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.databinding.BlogContentBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.storage.FirebaseStorage

class BlogContent : Fragment() {

    private lateinit var blogItem: BlogPost
    private lateinit var binding: BlogContentBinding

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        context = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.blog_content, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.event)

        blogItem = arguments?.getParcelable("blog") ?: BlogPost()

        val storage = FirebaseStorage.getInstance()
        val storageRef =
            storage.getReferenceFromUrl(blogItem.image)

        GlideApp.with(binding.blogImage!!.context)
            .load(storageRef)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
            .into(binding.blogImage)

        binding.authorPost.text = blogItem.author
        binding.datePost.text = convertToBlogStringDate(blogItem.publicationDate)
        binding.blogTitle.text = blogItem.title
        binding.blogSummary.text = blogItem.summary

        binding.visitPage.setOnClickListener {
            val uris = Uri.parse(blogItem.url)
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            startActivity(intents)
        }

        binding.shareLink.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SHARENOTE")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val intent = Intent(Intent.ACTION_SEND)
            val shareBody =
                getString(R.string.title) + ": " + blogItem.url
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.note))
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, getString(R.string.shareusing)))
        }
        return binding.root
    }
}