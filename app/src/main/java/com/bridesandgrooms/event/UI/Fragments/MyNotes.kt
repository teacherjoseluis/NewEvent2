package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.MVP.NotePresenter
import android.content.Context
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.SearchView
import android.widget.TextView
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.clone
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.NoteAdapter
import com.bridesandgrooms.event.UI.Fragments.MainActivity.Companion
import com.bridesandgrooms.event.databinding.MyNotesBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class MyNotes : Fragment(), NotePresenter.NoteActivity, NoteFragmentActionListener {

    private lateinit var recyclerViewAllNotes: RecyclerView
    private lateinit var presenternote: NotePresenter
    private lateinit var rvAdapter: NoteAdapter
    private lateinit var inf: MyNotesBinding

    private lateinit var toolbar: MaterialToolbar
    private lateinit var adView: AdView

    private var notesList = ArrayList<Note>()
    private var mContext: Context? = null
    private var showAds = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        showAds = RemoteConfigSingleton.get_showads()
        if (showAds) {
            MobileAds.initialize(requireContext()) { initializationStatus ->
                // You can leave this empty or handle initialization status if needed
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.notes_menu, menu)
        val shareNote = menu.findItem(R.id.share_note)
        shareNote.isVisible = false
        val deleteNote = menu.findItem(R.id.delete_note)
        deleteNote.isVisible = false

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false
        searchView.setOnSearchClickListener {}
        searchView.setOnCloseListener {
            toolbar.collapseActionView()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredModelList = ArrayList<Note>()
                filter(notesList, p0).forEach { note->filteredModelList.add(note) }
                rvAdapter = NoteAdapter(this@MyNotes, filteredModelList, mContext!!)
                recyclerViewAllNotes.adapter = rvAdapter
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.notes)

        inf = DataBindingUtil.inflate(inflater, R.layout.my_notes, container, false)

        if (showAds) {
            adView = inf.adView
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        recyclerViewAllNotes = inf.recyclerViewNotes
        recyclerViewAllNotes.apply {
            layoutManager = GridLayoutManager(inf.root.context, 2).apply {
                reverseLayout = true
            }
        }

        try {
            presenternote = NotePresenter(mContext!!, this)
            presenternote.getAllNotes()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }

        inf.floatingNewNote.setOnClickListener {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Add_Note")

            val fragment = NoteCreateEdit()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
                .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
                .commit()
        }
        return inf.root
    }

    private fun filter(models: ArrayList<Note>, query: String?): List<Note> {
        val lowerCaseQuery = query!!.toLowerCase(Locale.ROOT)
        val filteredModelList: ArrayList<Note> = ArrayList()
        for (model in models) {
            val textTitle: String = model.title.toLowerCase(Locale.ROOT)
            val textBody: String = model.body.toLowerCase(Locale.ROOT)
            if (textTitle.contains(lowerCaseQuery)||textBody.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }

    override fun onNoteSuccess(noteList: ArrayList<Note>) {
        if (noteList.size != 0) {
            notesList = clone(noteList)
            noteList.sortByDescending { it.lastupdateddatetime }

            try {
                rvAdapter = NoteAdapter(this@MyNotes, noteList, mContext!!)
                rvAdapter.notifyDataSetChanged()
            } catch (e: java.lang.Exception) {
                Log.e(TAG, e.message.toString())
            }
            recyclerViewAllNotes.adapter = null
            recyclerViewAllNotes.adapter = rvAdapter

            inf.withdata.visibility = ConstraintLayout.VISIBLE
//            val emptystateLayout = inf.withnodata
//            emptystateLayout.root.visibility = ConstraintLayout.GONE
        } else {
            emptyStateFragment()
        }
    }

    override fun onNoteError(errcode: String) {
        emptyStateFragment()
    }

    fun emptyStateFragment() {
        val container = inf.root as ViewGroup?
        container?.removeAllViews()

        val newView = layoutInflater.inflate(R.layout.empty_state_fragment, container, false)
        container?.addView(newView)

        newView.findViewById<TextView>(R.id.emptystate_message).setText(R.string.emptystate_nonotesmsg)
        newView.findViewById<TextView>(R.id.emptystate_cta).setText(R.string.emptystate_nonotescta)
        newView.findViewById<FloatingActionButton>(R.id.fab_action).setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "fab_action", "click")
            callNoteCreateFragment()
        }
    }

    fun callNoteCreateFragment(){
        val fragment = NoteCreateEdit()
        val bundle = Bundle()
        bundle.putString("calling_fragment", "EmptyState")
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        const val SCREEN_NAME = "My Notes"
        const val TAG = "MyNotes"
    }

    override fun onNoteFragmentWithData(note: Note) {
        val fragment = NoteCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("note", note)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment
            )
            .commit()
    }
}

interface NoteFragmentActionListener {
    fun onNoteFragmentWithData(note: Note)
}