package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Activities.ExportPDF.Companion.SCREEN_NAME

class GalleryAdapter(private val context: Context, private val dataList: List<Triple<Bitmap, String, String>>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_gallery_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val (bitmap, photographer, url) = dataList[position]
        holder.bind(bitmap, photographer, url)

        holder.itemView.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "itemView", "click")
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Explicitly using Google Chrome to handle the intent
            val packageManager = context.packageManager
            intent.setPackage("com.android.chrome") // Use this line to test with Chrome

            // Check if the Chrome is installed and can handle the intent
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback to the user choosing an application
                val chooser = Intent.createChooser(intent, "Choose a browser")
                if (chooser.resolveActivity(packageManager) != null) {
                    context.startActivity(chooser)
                } else {
                    Toast.makeText(context, "No browser found to open the link", Toast.LENGTH_LONG).show()
                    Log.e("com.bridesandgrooms.event.UI.Adapters.GalleryAdapter", "No application found to handle the URL: $url")
                }
            }
        }
    }


    override fun getItemCount(): Int = dataList.size

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.categoryimage)
        private val categorycredit: TextView? = itemView.findViewById(R.id.photo_credits)
        //private val photographerTextView: TextView = itemView.findViewById(R.id.categorytitle)


        fun bind(bitmap: Bitmap, photographer: String, url: String) {
            imageView.setImageBitmap(bitmap)
            categorycredit?.text = photographer
            //photographerTextView.text = photographer
        }
    }
}
