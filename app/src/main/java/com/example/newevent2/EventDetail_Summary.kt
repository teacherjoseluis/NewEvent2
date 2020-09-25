package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.eventdetail_summary.*
import kotlinx.android.synthetic.main.eventdetail_summary.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventDetail_Summary.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventDetail_Summary : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var eventkey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.eventdetail_summary, container, false)

        inf.cardView_venue.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskList::class.java)
            tasklist.putExtra("eventkey",eventkey)
            tasklist.putExtra("category","venue")
            startActivity(tasklist)
        }

        inf.cardView_photo.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskList::class.java)
            tasklist.putExtra("eventkey",eventkey)
            tasklist.putExtra("category","photo")
            startActivity(tasklist)
        }

        inf.cardView_entertainment.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskList::class.java)
            tasklist.putExtra("eventkey",eventkey)
            tasklist.putExtra("category","entertainment")
            startActivity(tasklist)
        }

        inf.cardView_flowers.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskList::class.java)
            tasklist.putExtra("eventkey",eventkey)
            tasklist.putExtra("category","flowers")
            startActivity(tasklist)
        }

        inf.floatingActionButton.setOnClickListener()
        {
            val newtask = Intent(activity, NewTask::class.java)
            newtask.putExtra("eventkey",eventkey)
            startActivity(newtask)
        }

        return inf
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventDetail_Summary.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventDetail_Summary().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}