package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.new_guest.*
import kotlinx.android.synthetic.main.new_guest.button

class NewGuest : AppCompatActivity() {

    var eventkey: String = ""
    private var uri: Uri? = null
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
            if (nameinputedit.text.toString().isEmpty()) {
                nameinputedit.error = "Guest name is required!"
            } else {
                saveguest()
            }
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
        if (uri != null) saveToInternalStorage()

        val guest = GuestEntity().apply {
            eventid = eventkey
            contactid = "local"
            rsvp = categoryrsvp
            companion = categorycompanions

            name = nameinputedit.text.toString()
            phone = phoneinputedit.text.toString()
            email = mailinputedit.text.toString()
            table = tableinputedit.text.toString()

            imageurl = uri?.let{it.toString()} ?: ""
        }

        guest.addGuest()
        finish()
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
                saveImage(applicationContext, uri)
            }
        } else {
            //system OS is < Marshmallow
            saveImage(applicationContext, uri)
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

