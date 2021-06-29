package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.TaskDBHelper

class rvCategoryAdapter(private val categorylist: List<Category>) :
    RecyclerView.Adapter<rvCategoryAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.category_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return categorylist.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.categorytitle?.text = categorylist[p1].en_name
        val resourceId = context.resources.getIdentifier(categorylist[p1].drawable, "drawable",
            context.packageName)
        p0.categoryimage?.setImageResource(resourceId)

        val taskdb = TaskDBHelper(context)
        val taskstats=taskdb.getCategoryStats(categorylist[p1].code)
        p0.taskpendinglabel?.text=taskstats.taskpending.toString()
        p0.taskdonelabel?.text=taskstats.taskcompleted.toString()
        p0.taskbudgetlabel?.text=taskstats.sumbudget.toString()

        p0.itemView.setOnClickListener {
            val catdetail = Intent(context, TaskPaymentList::class.java)
            catdetail.putExtra("category", categorylist[p1].code)
            context.startActivity(catdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categorytitle: TextView? = itemView.findViewById<TextView>(R.id.categorytitle)
        val categoryimage: ImageView? = itemView.findViewById<ImageView>(R.id.categoryimage)
        val taskpendinglabel: TextView? =itemView.findViewById<TextView>(R.id.taskpendinglabel)
        val taskdonelabel: TextView? =itemView.findViewById<TextView>(R.id.taskdonelabel)
        val taskbudgetlabel: TextView? =itemView.findViewById<TextView>(R.id.taskbudgetlabel)
    }
}



