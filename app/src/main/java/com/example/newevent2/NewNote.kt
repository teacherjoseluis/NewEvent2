package com.example.newevent2

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.new_note.*
import java.io.*
import java.lang.StringBuilder
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class NewNote : AppCompatActivity() {

    var eventkey = ""
    var notekey = ""
    var noteurl = ""

    lateinit var storage: FirebaseStorage

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_note)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        eventkey = intent.getStringExtra("eventkey").toString()
        notekey = intent.getStringExtra("notekey").toString()

        if (notekey != "null") {
            noteurl = intent.getStringExtra("noteurl").toString()
            eventkey = intent.getStringExtra("eventid").toString()
            val title = intent.getStringExtra("notetitle").toString()

            val notetitle = findViewById<TextView>(R.id.notetitle)
            notetitle.text = title

            storage = FirebaseStorage.getInstance()
            val storageRef =
                storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/User/Event/$eventkey/Note/$notekey/$noteurl")
            val localFile = File.createTempFile("notes", ".txt")
            storageRef.getFile(localFile).addOnSuccessListener {

                val fis = FileInputStream(localFile)
                var buffer = ByteArray(10)
                val sb = StringBuilder()
                while (fis.read(buffer) != -1) {
                    sb.append(String(buffer))
                    buffer = ByteArray(10)
                }
                fis.close()
                val editTextTextMultiLine = findViewById<EditText>(R.id.editTextTextMultiLine)
                editTextTextMultiLine.setText(sb.toString())

            }.addOnFailureListener {
                it.cause
            }
        }

        editTextTextMultiLine.setOnClickListener {
            editTextTextMultiLine.error = null
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (notetitle.text.toString().isEmpty()) {
                notetitle.error = "Note title is required!"
                inputvalflag = false
            }
            if (editTextTextMultiLine.text.toString().isEmpty()) {
                editTextTextMultiLine.error = "Note detail is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) ==
                        PackageManager.PERMISSION_DENIED
                    ) {
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        //show popup to request runtime permission
                        requestPermissions(permissions, PERMISSION_CODE)
                    } else {
                        //permission already granted
                        saveNote()
                    }
                }

                onBackPressed()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    saveNote()
                } else {
                    //permission from popup denied
                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        //Permission code
        private val PERMISSION_CODE = 1001
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveNote() {
        try {
            // Save multiline to text file
            val note = editTextTextMultiLine.text.toString()

            val timestamp = Time(System.currentTimeMillis())
            val notedatetime = Date(timestamp.time)
            val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
            val instant = timestamp.toInstant()

            val cw = ContextWrapper(this)
            var file = cw.getDir("notes", Context.MODE_PRIVATE)
            file = File(file, "BGNote_$instant.txt")

//            val file = File("{${getExternalFilesDir(null)!!.absolutePath}}/BGNote_$instant.txt")
//            file.createNewFile()

            val fos = OutputStreamWriter(FileOutputStream(file))
            fos.write(note)
            fos.flush()
            fos.close()

            val uri = Uri.fromFile(file)

            // Save textfile to storage
            val noteEntity = NoteEntity().apply {
                title = notetitle.text.toString()
                datetime = sdf.format(notedatetime)
                noteurl = uri.lastPathSegment.toString()
                eventid = eventkey
                key = notekey
                summary = note.substring(0, 50) + "..."
            }

            if (notekey != "null") {
                noteEntity.editNote(uri, noteurl)
            } else {
                noteEntity.addNote(uri)
            }

            // delete text file

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
