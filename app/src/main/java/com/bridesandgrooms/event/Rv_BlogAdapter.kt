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
import com.bridesandgrooms.event.Functions.Firebase.BlogPost
import Application.MyFirebaseApp
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.UI.Fragments.NoteFragmentActionListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.storage.FirebaseStorage
import com.firebase.ui.storage.images.FirebaseImageLoader

import com.google.firebase.storage.StorageReference

import com.bumptech.glide.module.AppGlideModule
import com.google.android.material.card.MaterialCardView
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

class Rv_BlogAdapter(
    private val fragmentActionListener: BlogFragmentActionListener,
    private val blogList: ArrayList<BlogPost>,
    val context: Context
) : RecyclerView.Adapter<Rv_BlogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0.context).inflate(R.layout.blog_item_layout, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val blog = blogList[p1]
        p0.bind(blog)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val blogCard: ConstraintLayout = itemView.findViewById(R.id.cardlayout)
        private val transitionDuration = 300L

        init {
            blogCard.setOnClickListener {
                handleClick()
            }
        }

        fun bind(blogPost: BlogPost) {
            blogCard.removeAllViews()

            val layoutId = R.layout.blog_item_layout
            val view =
                LayoutInflater.from(itemView.context).inflate(layoutId, blogCard, false)

            view.findViewById<TextView>(R.id.blogtitle).text = blogPost.title
            view.findViewById<TextView>(R.id.author).text = blogPost.author
            view.findViewById<TextView>(R.id.time).text = blogPost.readingTime

            val storage = FirebaseStorage.getInstance()
            val storageRef =
                storage.getReferenceFromUrl(blogPost.image)

            GlideApp.with(itemView.context)
                .load(storageRef)
                .into(view.findViewById(R.id.blogimage))

            Log.i("Image Loaded", "Image Loaded")

            blogCard.addView(view)
            animateView(view)
        }

        private fun animateView(view: View) {
            view.alpha = 0f // Set initial alpha to 0
            view.animate()
                .alpha(1f) // Animate alpha to 1
                .setDuration(transitionDuration)
                .start()
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val blog = blogList[position]
                fragmentActionListener.onBlogFragmentWithData(blog)
            }
        }
    }
}

