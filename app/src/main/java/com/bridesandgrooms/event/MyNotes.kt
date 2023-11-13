package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.MVP.NotePresenter
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.databinding.MyNotesBinding
import com.bridesandgrooms.event.UI.SwipeControllerTasks
import com.google.firebase.analytics.FirebaseAnalytics
//import kotlinx.android.synthetic.main.empty_state.view.*
//import kotlinx.android.synthetic.main.my_notes.view.*
//import kotlinx.android.synthetic.main.onboardingcard.view.*

class MyNotes : Fragment(), NotePresenter.NoteActivity, ItemSwipeListenerNote {

    private lateinit var presenternote: NotePresenter

    //private lateinit var rvAdapter: Rv_NoteAdapter
    private lateinit var inf: MyNotesBinding
    private lateinit var recyclerView: RecyclerView

    //private lateinit var recyclerView: RecyclerView
    private val REQUEST_CODE_NOTES = 4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inf = DataBindingUtil.inflate(inflater, R.layout.my_notes, container, false)
        try {
            presenternote = NotePresenter(requireContext(), this, inf.root)
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
        return inf.root
    }

    override fun onNoteSuccess(inflatedView: View, noteList: MutableList<Note>) {
        if (noteList.size != 0) {
            recyclerView = inf.recyclerViewNotes
            recyclerView.apply {
                layoutManager = GridLayoutManager(inflatedView.context, 2).apply {
                    reverseLayout = true
                }
            }
            val rvAdapter = Rv_NoteAdapter(noteList, this)
            recyclerView.adapter = rvAdapter

            val swipeController = SwipeControllerTasks(
                inflatedView.context,
                rvAdapter,
                recyclerView,
                null,
                RIGHTACTION
            )
            //----------------------------------------------------------------
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            //----------------------------------------------------------------
            inf.activenoteslayout.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.root.findViewById<ConstraintLayout>(R.id.withnodatanotes)
            emptystateLayout.visibility = ConstraintLayout.GONE
        } else if (noteList.size == 0) {
            inf.activenoteslayout.visibility = ConstraintLayout.GONE

            val emptystateLayout = inf.root.findViewById<ConstraintLayout>(R.id.withnodatanotes)
            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
            emptystateLayout.visibility = ConstraintLayout.VISIBLE

            inf.withnodatanotes.emptyCard.onboardingmessage.text = getString(R.string.emptystate_nonotesmsg)
            inf.withnodatanotes.newtaskbutton.startAnimation(fadeAnimation)
            inf.withnodatanotes.newtaskbutton.setOnClickListener {
                val newTask = Intent(context, NoteCreateEdit::class.java)
                newTask.putExtra("userid", "")
                startActivity(newTask)
            }
        }
    }

    override fun onNoteError(inflatedView: View, errcode: String) {
        inf.activenoteslayout.visibility = ConstraintLayout.GONE

        val emptystateLayout = inflatedView.findViewById<ConstraintLayout>(R.id.withnodatanotes)
        val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)

        emptystateLayout.visibility = ConstraintLayout.VISIBLE
        inf.withnodatanotes.emptyCard.onboardingmessage.text = getString(R.string.emptystate_nonotesmsg)
        inf.withnodatanotes.newtaskbutton.startAnimation(fadeAnimation)
        inf.withnodatanotes.newtaskbutton.setOnClickListener {
            val newTask = Intent(context, NoteCreateEdit::class.java)
            startActivity(newTask)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presenternote = NotePresenter(requireContext(), this, inf.root)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun onItemSwiped(noteList: MutableList<Note>) {
        if (noteList.isEmpty()) {
            // Show empty state
            inf.activenoteslayout.visibility = ConstraintLayout.GONE
            val emptystateLayout = inf.root.findViewById<ConstraintLayout>(R.id.withnodatanotes)
            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)

            emptystateLayout.visibility = ConstraintLayout.VISIBLE
            inf.withnodatanotes.emptyCard.onboardingmessage.text = getString(R.string.emptystate_nonotesmsg)
            inf.withnodatanotes.newtaskbutton.startAnimation(fadeAnimation)
            inf.withnodatanotes.newtaskbutton.setOnClickListener  {
                val newTask = Intent(context, NoteCreateEdit::class.java)
                newTask.putExtra("userid", "")
                startActivity(newTask)
            }
        } else {
            // Show RecyclerView
            inf.activenoteslayout.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.root.findViewById<ConstraintLayout>(R.id.withnodatanotes)
            emptystateLayout.visibility = ConstraintLayout.GONE
        }
    }

    companion object {
        const val RIGHTACTION = "delete"
        var notescreated_flag = 0
    }
}

