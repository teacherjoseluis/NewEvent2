package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.getlocale
import com.bridesandgrooms.event.Model.Category
import Application.MyFirebaseApp
import com.bridesandgrooms.event.MVP.DashboardEventPresenter
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.google.firebase.analytics.FirebaseAnalytics

class rvDashboardImageAdapter(private val dashboardImageList: List<DashboardEventPresenter.CategoryThumbnails>) :
    RecyclerView.Adapter<rvDashboardImageAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(p0.context).inflate(R.layout.dashboard_image_layout, p0, false)
        context = p0.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dashboardImageList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if (getlocale().substring(0,2) == "es") {//Getting the right substring depending the default language
            p0.categorytitle?.text = dashboardImageList[p1].category.nameEs
        } else {
            p0.categorytitle?.text = dashboardImageList[p1].category.nameEn
        }
        //Adds the image associated for each category
        p0.categoryimage?.setImageBitmap(dashboardImageList[p1].thumbnails?.get(0))
        p0.itemView.setOnClickListener {
//            val rvAdapter = rvImageGallerAdapter(images)
//            inf.categoryrv.adapter = rvAdapter
            val catdetail = Intent(context, ActivityGallery::class.java)
            catdetail.putExtra("category", dashboardImageList[p1].category.code)
            context.startActivity(catdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categorytitle: TextView? = itemView.findViewById(R.id.categorytitle)
        val categoryimage: ImageView? = itemView.findViewById(R.id.categoryimage)
    }
}



