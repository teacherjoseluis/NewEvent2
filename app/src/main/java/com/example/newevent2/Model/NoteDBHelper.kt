package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.text.Normalizer
import kotlin.collections.ArrayList

class NoteDBHelper(context: Context) {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var note: Note
    var key = ""

    fun insert(note: Note) {
        var values = ContentValues()
//        values.put("noteid", note.noteid)
        //removing accents
        note.title = Normalizer.normalize(note.title, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+", "")
        values.put("title", note.title)
        //removing accents
        note.body = Normalizer.normalize(note.body, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+", "")
        values.put("body", note.body)
        values.put("color", note.color)
        values.put("lastupdateddatetime", note.lastupdateddatetime)
        db.insert("NOTE", null, values)
        Log.d(TAG, "Note record inserted")
    }

    fun getNoteexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM NOTE WHERE noteid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        return existsflag
    }

    fun getAllNotes(): ArrayList<Note> {
        val list = ArrayList<Note>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM NOTE ORDER BY lastupdateddatetime DESC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val noteid = cursor.getString(cursor.getColumnIndex("noteid"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val body = cursor.getString(cursor.getColumnIndex("body"))
                    val color = cursor.getString(cursor.getColumnIndex("color"))
                    val lastupdateddatetime = cursor.getString(cursor.getColumnIndex("lastupdateddatetime"))

                    val note =
                        Note(noteid, title, body, color, lastupdateddatetime)
                    list.add(note)
                    Log.d(TAG, "Note $noteid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun update(note: Note) {
        var values = ContentValues()
        note.title = Normalizer.normalize(note.title, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+", "")
        values.put("title", note.title)
        //removing accents
        note.body = Normalizer.normalize(note.body, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+", "")
        values.put("body", note.body)
        values.put("color", note.color)
        values.put("lastupdateddatetime", note.lastupdateddatetime)

        val retVal = db.update("NOTE", values, "noteid = '${note.noteid}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Note ${note.noteid} updated")
        } else {
            Log.d(TAG, "Guest ${note.noteid} not updated")
        }
        db.close()
    }

    fun delete(note: Note) {
        val retVal = db.delete("NOTE", "noteid = '${note.noteid}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Note ${note.noteid} deleted")
        } else {
            Log.d(TAG, "Note ${note.noteid} not deleted")
        }
        db.close()
    }

    companion object {
        const val TAG = "NoteDBHelper"
    }
}
