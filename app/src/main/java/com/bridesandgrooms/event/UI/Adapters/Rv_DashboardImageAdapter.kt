package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.getlocale
import com.bridesandgrooms.event.MVP.DashboardEventPresenter
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.PaymentAdapter.Companion.SCREEN_NAME
import com.bridesandgrooms.event.UI.Fragments.GalleryFragmentActionListener
import com.google.android.material.card.MaterialCardView

class rvDashboardImageAdapter(
    private val fragmentActionListener: GalleryFragmentActionListener,
    private val dashboardImageList: List<DashboardEventPresenter.CategoryThumbnails>,
    val context: Context
) :
    RecyclerView.Adapter<rvDashboardImageAdapter.ViewHolder>() {

    private lateinit var view: View

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        view =
            LayoutInflater.from(p0.context).inflate(R.layout.dashboard_image_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dashboardImageList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val dashboardImage = dashboardImageList[p1]
        p0.bind(dashboardImage)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryCardView: MaterialCardView = itemView.findViewById(R.id.CategoryCardView)
        val categorytitle: TextView? = itemView.findViewById(R.id.categorytitle)
        val categoryimage: ImageView? = itemView.findViewById(R.id.categoryimage)

        init {
            categoryCardView.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "categoryCardView", "click")
                handleClick()
            }
        }

        fun bind(dashboardImage: DashboardEventPresenter.CategoryThumbnails) {
            if (getlocale().substring(
                    0,
                    2
                ) == "es"
            ) {//Getting the right substring depending the default language
                categorytitle?.text = dashboardImage.category.nameEs
            } else {
                categorytitle?.text = dashboardImage.category.nameEn
            }
            //Adds the image associated for each category
            categoryimage?.setImageBitmap(dashboardImage.thumbnails?.get(0))

//            p0.itemView.setOnClickListener {
////            val rvAdapter = rvImageGallerAdapter(images)
////            inf.categoryrv.adapter = rvAdapter
//                val catdetail = Intent(context, FragmentGallery::class.java)
//                catdetail.putExtra("category", dashboardImageList[p1].category.code)
//                context.startActivity(catdetail)
//            }
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val category = dashboardImageList[position]
                fragmentActionListener.onGalleryFragmentWithData(category.category.code)
            }
        }
    }
}



