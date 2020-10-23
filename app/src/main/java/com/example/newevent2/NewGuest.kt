package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.floatingActionButton
import kotlinx.android.synthetic.main.new_guest.*
import kotlinx.android.synthetic.main.new_guest.button
import kotlinx.android.synthetic.main.new_task_taskdetail.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class NewGuest : AppCompatActivity() {

    var eventkey: String = ""
    private lateinit var uri: Uri
    private var chiptextvalue: String? = null
    private var categoryrsvp: String = ""
    private var categorycompanions: String = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_guest)

        eventkey = intent.getStringExtra("eventkey").toString()

        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        mPhoneNumber.setOnFocusChangeListener { p0, p1 ->
            PhoneNumberUtils.formatNumber(
                mPhoneNumber.text.toString(),
                tm.simCountryIso
            )
        }

        floatingActionButton.setOnClickListener {
            showImagePickerDialog()
        }

        button.setOnClickListener {
            saveguest()

        }
    }

    private fun saveguest() {

        if (rsvpgroup.checkedChipId != null) {
            var id = rsvpgroup.checkedChipId
            var chipselected = rsvpgroup.findViewById<Chip>(id)
            chiptextvalue = chipselected.text.toString()
            categoryrsvp = when (chiptextvalue) {
                "Yes" -> "y"
                "No" -> "n"
                "Pending" -> "pending"
                else -> "pending"
            }

            id = companionsgroup.checkedChipId
            chipselected = companionsgroup.findViewById<Chip>(id)
            chiptextvalue = chipselected.text.toString()
            categorycompanions = when (chiptextvalue) {
                "Adult" -> "adult"
                "Child" -> "child"
                "Baby" -> "baby"
                "None" -> "none"
                else -> "none"
            }
        }

        //val cropbitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        saveToInternalStorage()

        val guest = GuestEntity()
        guest.eventid = eventkey
        guest.contactid = "local"
        guest.rsvp = categoryrsvp
        guest.companion = categorycompanions

        guest.name = nameinputedit.text.toString()
        guest.phone = phoneinputedit.text.toString()
        guest.email = mailinputedit.text.toString()
        guest.table = tableinputedit.text.toString()

        guest.imageurl = uri.toString()

        guest.addGuest()
    }

    private fun saveToInternalStorage() {
        //Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, NewGuest.PERMISSION_CODE)
            } else {
                //permission already granted
                saveImage()
            }
        } else {
            //system OS is < Marshmallow
            saveImage()
        }
    }

    private fun saveImage() {

        val cw = ContextWrapper(applicationContext)

        var file = cw.getDir("images", Context.MODE_PRIVATE)
        file = File(file, uri.lastPathSegment.toString())


        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)

            val fos = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, fos)
            Log.i("File Saved",file.toString())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showImagePickerDialog() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        //Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, NewGuest.PERMISSION_CODE)
            } else {
                //permission already granted
                startActivityForResult(intent, NewGuest.IMAGE_PICK_CODE)
            }
        } else {
            //system OS is < Marshmallow
            startActivityForResult(intent, NewGuest.IMAGE_PICK_CODE)
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        internal val PERMISSION_CODE = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MainActivity.PERMISSION_CODE -> {
                if (grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    showImagePickerDialog()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == NewGuest.IMAGE_PICK_CODE) {
            uri = data?.data!!
            CropImage.activity(uri)
                .setMinCropResultSize(80, 80)
                .setMaxCropResultSize(800, 800)
                .start(this)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                uri = result.uri
                contactavatar.setImageURI(uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}

