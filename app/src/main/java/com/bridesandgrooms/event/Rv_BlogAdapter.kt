package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bridesandgrooms.event.Functions.BlogPost
import Application.MyFirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.storage.FirebaseStorage
import com.firebase.ui.storage.images.FirebaseImageLoader

import com.google.firebase.storage.StorageReference

import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream


@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}

class Rv_BlogAdapter(private val blogList: ArrayList<BlogPost>) :
    RecyclerView.Adapter<Rv_BlogAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0.context).inflate(R.layout.blog_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        //p0.blogtitle?.text = texttrimming(context, blogList[p1].title, BLOGTITLEWIDTH)
        p0.blogtitle?.text = blogList[p1].title
        p0.author?.text = blogList[p1].author
        //p0.date?.text = blogList[p1].publicationDate
        p0.time?.text = blogList[p1].readingTime

        val storage = FirebaseStorage.getInstance()
        val storageRef =
            storage.getReferenceFromUrl(blogList[p1].image)

        GlideApp.with(p0.blogimage!!.context)
            .load(storageRef)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
            .into(p0.blogimage)

        Log.i("Image Loaded", "Image Loaded")

//        p0.shareLink.setOnClickListener {
//            // ------- Analytics call ----------------
//            val bundle = Bundle()
//            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SHARENOTE")
//            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
//            //----------------------------------------
//
//            val intent = Intent(Intent.ACTION_SEND)
//            val shareBody =
//                getString(R.string.title) + ": " + blogList[p1].url
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.note))
//            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
//            startActivity(Intent.createChooser(intent, getString(R.string.shareusing)))
//        }


        p0.itemView.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "BLOGPOST")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, blogList[p1].title)
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

//            val uris = Uri.parse(blogList[p1].url)
//            val intents = Intent(Intent.ACTION_VIEW, uris)
//            val b = Bundle()
//            b.putBoolean("new_window", true)
//            intents.putExtras(b)
//            context.startActivity(intents)
            val blogDetail = Intent(context, BlogContent::class.java)
            blogDetail.putExtra("blog", blogList[p1])
            context.startActivity(blogDetail)
        }
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blogtitle: TextView? = itemView.findViewById(R.id.blogtitle)
        val author: TextView? = itemView.findViewById(R.id.author)
        //val date: TextView? = itemView.findViewById(R.id.date)
        val time: TextView? = itemView.findViewById(R.id.time)
        val blogimage: ImageView? = itemView.findViewById(R.id.blogimage)
    }

    companion object {
        const val BLOGTITLEWIDTH = 344F
    }
}

