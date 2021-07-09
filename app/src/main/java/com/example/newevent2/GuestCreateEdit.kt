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
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.addTask
import com.example.newevent2.Functions.editGuest
import com.example.newevent2.Functions.editTask
import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.GuestModel
import com.example.newevent2.ui.TextValidate
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.new_guest.*
import kotlinx.android.synthetic.main.new_guest.button

class GuestCreateEdit : AppCompatActivity() {

    //var eventkey: String = ""
    private var uri: Uri? = null
    private var chiptextvalue: String? = null
    private var categoryrsvp: String = ""
    private var categorycompanions: String = ""

    private lateinit var guestitem: Guest

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_guest)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "New Guest"

        val extras = intent.extras
        guestitem = if (extras!!.containsKey("guest")) {
            intent.getParcelableExtra("guest")!!
        } else {
            Guest()
        }

        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val guestid = guestitem.key

        if (guestid != "") {
            val guestmodel = GuestModel()
            val user = com.example.newevent2.Functions.getUserSession(applicationContext!!)
            guestmodel.getGuestdetail(
                user.key,
                user.eventid,
                guestid,
                object : GuestModel.FirebaseSuccessGuest {
                    override fun onGuest(guest: Guest) {
                        nameinputedit.setText(guest.name)
                        phoneinputedit.setText(guest.phone)
                        tableinputedit.setText(guest.table)

                        guestitem.key = guest.key
                        guestitem.name = guest.name
                        guestitem.phone = guest.phone
                        guestitem.email = guest.email
                        guestitem.rsvp = guest.rsvp
                        guestitem.companion = guest.companion
                        guestitem.table = guest.table
                    }
                })
        }

        nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(nameinputedit).namefieldValidate()
                if (validationmessage != "") {
                    nameinputedit.error = "Error in Task name: $validationmessage"
                }
            }
        }

        nameinputedit.setOnClickListener {
            nameinputedit.error = null
        }

        phoneinputedit.setOnClickListener {
            phoneinputedit.error = null
        }

        mPhoneNumber.setOnFocusChangeListener { p0, p1 ->
            PhoneNumberUtils.formatNumber(
                mPhoneNumber.text.toString(),
                tm.simCountryIso
            )
        }

        tableinputedit.setOnClickListener {
            tableinputedit.error = null
        }

//        floatingActionButton.setOnClickListener {
//            showImagePickerDialog()
//        }

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

        //if (uri != null) saveToInternalStorage()


        button.setOnClickListener {
            var inputvalflag = true
            if (nameinputedit.text.toString().isEmpty()) {
                nameinputedit.error = "Guest name is required!"
                inputvalflag = false
            }
            if (phoneinputedit.text.toString().isEmpty()) {
                phoneinputedit.error = "Guest phone is required!"
                inputvalflag = false
            }
            if (rsvpgroup.checkedChipId == -1) {
                Toast.makeText(this, "RSVP is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (companionsgroup.checkedChipId == -1) {
                Toast.makeText(this, "Companion info is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (tableinputedit.text.toString().isEmpty()) {
                Toast.makeText(this, "Table info is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                saveguest()
                finish()
            }
        }
    }

    private fun saveguest() {

        guestitem.rsvp = categoryrsvp
        guestitem.companion = categorycompanions
        guestitem.table = tableinputedit.text.toString()
        guestitem.name = nameinputedit.text.toString()
        guestitem.phone = phoneinputedit.text.toString()
        guestitem.email = mailinputedit.text.toString()

        if (guestitem.key == "") {
            addGuest(this, guestitem)
        } else if (guestitem.key != "") {
            editGuest(applicationContext, guestitem)
        }
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


//    private fun saveToInternalStorage() {
//        //Request permissions
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                PackageManager.PERMISSION_DENIED
//            ) {
//                //permission denied
//                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                //show popup to request runtime permission
//                requestPermissions(permissions, GuestCreateEdit.PERMISSION_CODE)
//            } else {
//                //permission already granted
//                saveImage(applicationContext, uri)
//            }
//        } else {
//            //system OS is < Marshmallow
//            saveImage(applicationContext, uri)
//        }
//    }

//    private fun showImagePickerDialog() {
//        //Intent to pick image
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//
//        //Request permissions
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
//                PackageManager.PERMISSION_DENIED
//            ) {
//                //permission denied
//                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
//                //show popup to request runtime permission
//                requestPermissions(permissions, NewGuest.PERMISSION_CODE)
//            } else {
//                //permission already granted
//                startActivityForResult(intent, NewGuest.IMAGE_PICK_CODE)
//            }
//        } else {
//            //system OS is < Marshmallow
//            startActivityForResult(intent, NewGuest.IMAGE_PICK_CODE)
//        }
//    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            MainActivity.PERMISSION_CODE -> {
//                if (grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED
//                ) {
//                    //permission from popup granted
//                    showImagePickerDialog()
//                } else {
//                    //permission from popup denied
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == Activity.RESULT_OK && requestCode == GuestCreateEdit.IMAGE_PICK_CODE) {
//            uri = data?.data!!
//            CropImage.activity(uri)
//                .setMinCropResultSize(80, 80)
//                .setMaxCropResultSize(800, 800)
//                .start(this)
//        }
//
//        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == Activity.RESULT_OK) {
//                uri = result.uri
//                contactavatar.setImageURI(uri)
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error = result.error
//            }
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        internal val PERMISSION_CODE = 1001
    }
}

