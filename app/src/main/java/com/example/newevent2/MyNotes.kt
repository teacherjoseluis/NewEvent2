package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newevent2.MVP.NotePresenter
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.Note
import com.example.newevent2.Model.NoteDBHelper
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.my_notes.*
import kotlinx.android.synthetic.main.my_notes.view.*

class MyNotes : Fragment(), NotePresenter.NoteActivity {

    private lateinit var presenternote: NotePresenter
    private lateinit var rvAdapter: Rv_NoteAdapter
    private var recyclerView = recyclerViewNotes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.my_notes, container, false)

        recyclerView = inf.recyclerViewNotes
        recyclerView.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
        presenternote = NotePresenter(requireContext(), this, inf)

        inf.floatingNewNote.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWNOTE")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val newnote = Intent(context, NoteCreateEdit::class.java)
            newnote.putExtra("userid", "")
            startActivity(newnote)
        }
        return inf
    }

    override fun onResume() {
        super.onResume()
        if (notescreated_flag== 1) {
            val notedb = NoteDBHelper(requireContext())
            val notelist = notedb.getAllNotes()
            if (notelist.size == 0) {
                this.withdata.visibility = ConstraintLayout.GONE
                this.withnodata.visibility = ConstraintLayout.VISIBLE
            }
            rvAdapter = Rv_NoteAdapter(notelist)
            recyclerView.adapter = rvAdapter
            notescreated_flag = 0
        }
    }

    override fun onNoteSuccess(inflatedView: View, notelist: ArrayList<Note>) {
        rvAdapter = Rv_NoteAdapter(notelist)
        recyclerView.adapter = rvAdapter

        val swipeController = SwipeControllerTasks(
            requireContext(),
            rvAdapter,
            recyclerView,
            null,
            RIGHTACTION
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onNoteError(inflatedView: View, errcode: String) {
        inflatedView.withdata.visibility = ConstraintLayout.GONE
        inflatedView.withnodata.visibility = ConstraintLayout.VISIBLE
    }

    companion object {
        const val RIGHTACTION = "delete"
            var notescreated_flag = 0
    }
}

