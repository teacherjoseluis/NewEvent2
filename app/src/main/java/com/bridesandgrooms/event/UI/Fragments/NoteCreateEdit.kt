package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import Application.GuestDeletionException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.Model.NoteDBHelper
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.databinding.NewNoteBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat

class NoteCreateEdit : Fragment() {

    private lateinit var noteitem: Note
    private lateinit var binding: NewNoteBinding
    private lateinit var user: User

    private lateinit var context: Context
    private lateinit var notedb: NoteDBHelper

    private val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus && view is TextInputEditText) {
            val parentLayout = view.parent.parent as? TextInputLayout
            parentLayout?.error = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        context = requireContext()
        notedb = NoteDBHelper(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = User().getUser(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.new_note, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.new_note)

        noteitem = arguments?.getParcelable("note") ?: Note()

        if (noteitem.noteid.isNotEmpty()) {
            toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.edit_note)
            binding.notetitle.setText(noteitem.title)
            binding.notebody.setText(noteitem.body)
        }

        binding.notetitle.onFocusChangeListener = focusChangeListener
        binding.notebody.onFocusChangeListener = focusChangeListener

        binding.button.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Save_Note")
            val isValid = validateAllInputs()
            if (isValid) {
                savenote()
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (noteitem.noteid.isNotEmpty()) {
            inflater.inflate(R.menu.notes_menu, menu)
            menu.findItem(R.id.share_note).isEnabled = true
            menu.findItem(R.id.delete_note).isEnabled = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share_note -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Share Note")
                val intent = Intent(Intent.ACTION_SEND)
                val shareBody =
                    getString(R.string.title) + ": " + binding.notetitle.text.toString() + getString(
                        R.string.note
                    ) + ": " + binding.notebody.text.toString()
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.note))
                intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(intent, getString(R.string.shareusing)))
                true
            }

            R.id.delete_note -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Delete_Guest")
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Note")
                        try {
                            notedb.delete(noteitem)
                            finish()
                        } catch (e: GuestDeletionException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                e.message.toString(),
                                "deleteGuest()",
                                e.stackTraceToString()
                            )
                            Log.e(TAG, e.message.toString())
                        }
                        finish()

                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun validateAllInputs(): Boolean {
        var isValid = true
        val validator = InputValidator(context)

        val nameValidation =
            validator.validate(binding.notetitle)
        if (!nameValidation) {
            binding.notetitle.error = validator.errorCode
            isValid = false
        }

        val bodyValidation =
            validator.validate(binding.notebody)
        if (!bodyValidation) {
            binding.notebody.error = validator.errorCode
            isValid = false
        }
        return isValid
    }

    private fun savenote() {
        val timestamp = Time(System.currentTimeMillis())
        val notedatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")

        noteitem.title = binding.notetitle.text.toString()
        noteitem.body = binding.notebody.text.toString()
        noteitem.lastupdateddatetime = sdf.format(notedatetime)

        if (noteitem.noteid.isEmpty()) {
            try {
                notedb.insert(noteitem)
            } catch (e: Exception) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "addNote()",
                    e.stackTraceToString()
                )
                Log.e(TAG, e.message.toString())
            }
        } else {
            try {
                notedb.update(noteitem)
            } catch (e: Exception) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "editNote()",
                    e.stackTraceToString()
                )
                Log.e(TAG, e.message.toString())
            }
        }
        finish()
    }

    fun finish() {
        val fragment = MyNotes()
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }, 500)
    }

    companion object {
        const val SCREEN_NAME = "Note_CreateEdit"
        const val TAG = "NoteCreateEdit"
    }
}
