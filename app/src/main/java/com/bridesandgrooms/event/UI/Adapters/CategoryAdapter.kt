package com.bridesandgrooms.event.UI.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.getlocale
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Category.Companion.getCategory
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Fragments.CategoryFragmentActionListener
import com.google.android.material.card.MaterialCardView

class CategoryAdapter(
    private val fragmentActionListener: CategoryFragmentActionListener,
    private val categorylist: List<Category>,
    val context: Context
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private lateinit var view: View

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CategoryViewHolder {
        val category_layout = RemoteConfigSingleton.get_category_layout()
        view = if (category_layout.equals("card")) {
            LayoutInflater.from(p0.context).inflate(R.layout.category_item_layout, p0, false)
        } else {
            LayoutInflater.from(p0.context)
                .inflate(R.layout.category_listitem_layout, p0, false)
        }
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categorylist.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categorylist[position]
        holder.bind(category)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryCardView: MaterialCardView = itemView.findViewById(R.id.CategoryCardView)
        val categorytitle: TextView? = itemView.findViewById(R.id.categorytitle)
        val categoryimage: ImageView? = itemView.findViewById(R.id.categoryimage)

        init {
            categoryCardView.setOnClickListener{
                handleClick()
            }
        }

        fun bind(category: Category) {
            if (getlocale().substring(
                    0,
                    2
                ) == "es"
            ) {//Getting the right substring depending the default language
                categorytitle?.text = category.es_name
            } else {
                categorytitle?.text = category.en_name
            }
            //Adds the image associated for each category
            val resourceId = context.resources.getIdentifier(
                getCategory(category.code).drawable, "drawable",
                context.packageName
            )
            categoryimage?.setImageResource(resourceId)
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val category = categorylist[position]
                fragmentActionListener.onCategoryFragmentWithData(category.code)
            }
        }
    }
}



