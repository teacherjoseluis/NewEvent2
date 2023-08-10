package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.MVP.NotePresenter
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.Model.Note
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.my_notes.view.*
import kotlinx.android.synthetic.main.my_notes.view.withnodata
import kotlinx.android.synthetic.main.onboardingcard.view.*

class MyNotes : Fragment(), NotePresenter.NoteActivity {

    private lateinit var presenternote: NotePresenter
   //private lateinit var rvAdapter: Rv_NoteAdapter
    private lateinit var inf: View
    //private lateinit var recyclerView: RecyclerView
    private val REQUEST_CODE_NOTES = 4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inf = inflater.inflate(R.layout.my_notes, container, false)
//        recyclerView = inf.recyclerViewNotes
//        recyclerView.apply {
//            layoutManager = GridLayoutManager(inf.context, 2).apply {
//                reverseLayout = true
//            }
//        }
        try {
            presenternote = NotePresenter(requireContext(), this, inf)
        } catch (e: Exception) {
            println(e.message)
        }

        inf.floatingNewNote.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWNOTE")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val newnote = Intent(context, NoteCreateEdit::class.java)
            newnote.putExtra("userid", "")
            startActivityForResult(newnote, REQUEST_CODE_NOTES)
        }
        return inf
    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if ((requestCode == REQUEST_CODE_NOTES) && (resultCode == Activity.RESULT_OK)) {
//            //val guestarray = data?.getSerializableExtra("guests") as ArrayList<Guest>
//            try {
//                presenternote = NotePresenter(requireContext(), this, inf)
//            } catch (e: Exception) {
//                println(e.message)
//            }
//        }
//    }

    override fun onNoteSuccess(inflatedView: View, notelist: ArrayList<Note>) {
        if (notelist.size != 0) {
        val recyclerView= inflatedView.recyclerViewNotes

        recyclerView.apply {
            layoutManager = GridLayoutManager(inflatedView.context, 2).apply {
                reverseLayout = true
            }
        }
            val rvAdapter = Rv_NoteAdapter(notelist)
            recyclerView.adapter = rvAdapter

        val swipeController = SwipeControllerTasks(
            inflatedView.context,
            rvAdapter,
            recyclerView,
            null,
            RIGHTACTION
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)
            inflatedView.withdata.visibility = ConstraintLayout.VISIBLE
            inflatedView.withnodata.visibility = ConstraintLayout.GONE

        } else if (notelist.size == 0) {
            inflatedView.withdata.visibility = ConstraintLayout.GONE

            val emptystateLayout = inflatedView.findViewById<ConstraintLayout>(R.id.withnodata)
            val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
            val bottomMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
            val params = emptystateLayout.layoutParams as ViewGroup.MarginLayoutParams

            params.topMargin = topMarginInPixels
            params.bottomMargin = bottomMarginInPixels
            emptystateLayout.layoutParams = params

            emptystateLayout.visibility = ConstraintLayout.VISIBLE
            emptystateLayout.onboardingmessage.text = getString(R.string.emptystate_nonotesmsg)

            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)

            emptystateLayout.newtaskbutton.setOnClickListener {
                val newTask = Intent(context, NoteCreateEdit::class.java)
                newTask.putExtra("userid", "")
                startActivity(newTask)
            }
        }
    }

    override fun onNoteError(inflatedView: View, errcode: String) {
        inflatedView.withdata.visibility = ConstraintLayout.GONE

        val emptystateLayout = inflatedView.findViewById<ConstraintLayout>(R.id.withnodata)
        val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
        val bottomMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
        val params = emptystateLayout.layoutParams as ViewGroup.MarginLayoutParams

        params.topMargin = topMarginInPixels
        params.bottomMargin = bottomMarginInPixels
        emptystateLayout.layoutParams = params

        emptystateLayout.visibility = ConstraintLayout.VISIBLE
        emptystateLayout.onboardingmessage.text = getString(R.string.emptystate_nonotesmsg)

        val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
        emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)

        emptystateLayout.newtaskbutton.setOnClickListener {
            val newTask = Intent(context, NoteCreateEdit::class.java)
            startActivity(newTask)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presenternote = NotePresenter(requireContext(), this, inf)
        } catch (e: Exception) {
            println(e.message)
        }
//        recyclerViewActive.adapter = null
//        recyclerViewActive.adapter = rvAdapter
    }

    companion object {
        const val RIGHTACTION = "delete"
        var notescreated_flag = 0
    }
}

