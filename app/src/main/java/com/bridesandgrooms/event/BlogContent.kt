package com.bridesandgrooms.event

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Functions.BlogPost
import com.bridesandgrooms.event.Functions.convertToBlogStringDate
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.databinding.BlogContentBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.storage.FirebaseStorage

class BlogContent : AppCompatActivity() {

    private lateinit var blogItem: BlogPost
    private lateinit var binding: BlogContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.blog_content)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        blogItem = if (extras?.containsKey("blog") == true) {
            intent.getParcelableExtra("blog")!!
        } else {
            BlogPost()
        }

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
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }
}