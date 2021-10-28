package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.TaskDBHelper
import com.google.firebase.analytics.FirebaseAnalytics

class rvCategoryAdapter(private val categorylist: List<Category>) :
    RecyclerView.Adapter<rvCategoryAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.category_item_layout, p0, false)
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
        //Adds the image associated for each category
        val resourceId = context.resources.getIdentifier(categorylist[p1].drawable, "drawable",
            context.packageName)
        p0.categoryimage?.setImageResource(resourceId)

        val taskdb = TaskDBHelper(context)
        // The below function gets the statistics for tasks and budgets associated to each category
        val taskstats=taskdb.getCategoryStats(categorylist[p1].code)
        p0.taskpendinglabel?.text=taskstats.taskpending.toString()
        p0.taskdonelabel?.text=taskstats.taskcompleted.toString()
        p0.taskbudgetlabel?.text= taskstats.sumbudget

        p0.itemView.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "CATEGORYCARD")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, categorylist[p1].code)
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
            // This opens the list of Task and Payments for the selected category
            val catdetail = Intent(context, TaskPaymentList::class.java)
            catdetail.putExtra("category", categorylist[p1].code)
            context.startActivity(catdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categorytitle: TextView? = itemView.findViewById(R.id.categorytitle)
        val categoryimage: ImageView? = itemView.findViewById(R.id.categoryimage)
        val taskpendinglabel: TextView? =itemView.findViewById(R.id.taskpendinglabel)
        val taskdonelabel: TextView? =itemView.findViewById(R.id.taskdonelabel)
        val taskbudgetlabel: TextView? =itemView.findViewById(R.id.taskbudgetlabel)
    }
}



