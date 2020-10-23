package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.button
import kotlinx.android.synthetic.main.activity_main.floatingActionButton
import kotlinx.android.synthetic.main.new_guest.*
import kotlinx.android.synthetic.main.task_editdetail.*
import com.example.newevent2.saveImage

class Guest_EditDetail : AppCompatActivity() {

    private lateinit var uri: Uri
    private var localflag = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_guest)
        val intent = intent

        val guestitem = GuestEntity().apply {
            contactid = intent.getStringExtra("contactid").toString()
            rsvp = intent.getStringExtra("rsvp").toString()
            companion = intent.getStringExtra("companion").toString()
            table = intent.getStringExtra("table").toString()
            //key = intent.getStringExtra("key").toString()
            name = intent.getStringExtra("name").toString()
            imageurl = intent.getStringExtra("imageurl").toString()
            uri = Uri.parse(intent.getStringExtra("imageurl").toString())
            phone = intent.getStringExtra("phone").toString()
            email = intent.getStringExtra("email").toString()
        }

        var contactname: String? = null
        var contactphoto: String? = null
        var contactphone: String? = null
        var contactemail: String? = null
        var cursor: Cursor? = null

        if (guestitem.contactid == "local") localflag=true

        val whereclause = StringBuffer()
        whereclause.append(ContactsContract.Contacts._ID)
        whereclause.append(" = ")
        whereclause.append(guestitem.contactid)



        if (!localflag) {
            cursor =
                contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    whereclause.toString(),
                    null, null
                )

            cursor?.moveToNext()
            contactname =
                cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                    .toString()
            contactphoto =
                cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                    .toString()
            contactphone=
                cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    .toString()
            contactemail=
                cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID))
                    .toString()

            cursor?.let { cursor.close() }
        }

        val camerabutton = findViewById<ImageView>(R.id.floatingActionButton).also {
            if (!localflag) it.isVisible = false
        }
        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit).also {
            if (!localflag) it.isEnabled=false
        }
        mPhoneNumber.setText(contactphone?.let{it} ?: guestitem.phone)

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val avatar = findViewById<ImageView>(R.id.contactavatar)
        avatar.setImageURI(contactphoto?.let { it.toUri() } ?: guestitem.imageurl.toUri())


        val nameinput = findViewById<TextInputEditText>(R.id.nameinputedit).also {
            if (!localflag) it.isEnabled=false
        }
        nameinput.setText(contactname?.let { it } ?: guestitem.name)


        val emailinput = findViewById<TextInputEditText>(R.id.mailinputedit).also {
            if (!localflag) it.isEnabled=false
        }
        emailinput.setText(contactemail?.let{it} ?: guestitem.email)

        var selectedchiprsvp = when (guestitem.rsvp) {
            "y" -> findViewById<TextView>(R.id.chip)
            "n" -> findViewById<TextView>(R.id.chip2)
            "pending" -> findViewById<TextView>(R.id.chip3)
            else -> findViewById<TextView>(R.id.chip3)
        }

        rsvpgroup.check(selectedchiprsvp.id)
        selectedchiprsvp.isSelected = true

        var selectedchipcompanions = when (guestitem.companion) {
            "adult" -> findViewById<TextView>(R.id.chip4)
            "child" -> findViewById<TextView>(R.id.chip5)
            "baby" -> findViewById<TextView>(R.id.chip6)
            "none" -> findViewById<TextView>(R.id.chip7)
            else -> findViewById<TextView>(R.id.chip7)
        }

        companionsgroup.check(selectedchipcompanions.id)
        selectedchipcompanions.isSelected = true

        val tableinput = findViewById<TextInputEditText>(R.id.tableinputedit)
        tableinput.setText(guestitem.table)

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
            editguest()
        }

    }

    private fun editguest(){

        var chiptextvalue : String
        val guest = GuestEntity()
        guest.key = intent.getStringExtra("key").toString()
        guest.eventid = intent.getStringExtra("eventid").toString()

        if(localflag) {
            if(uri.toString() != intent.getStringExtra("imageurl").toString()) saveImage(applicationContext, uri)
            guest.imageurl = uri.toString()
            guest.name = nameinputedit.text.toString()
            guest.phone = phoneinputedit.text.toString()
            guest.email = mailinputedit.text.toString()
        }
        guest.table = tableinputedit.text.toString()

        if (rsvpgroup.checkedChipId != null) {
            var id = rsvpgroup.checkedChipId
            var chipselected = rsvpgroup.findViewById<Chip>(id)
            chiptextvalue = chipselected.text.toString()
            guest.rsvp = when (chiptextvalue) {
                "Yes" -> "y"
                "No" -> "n"
                "Pending" -> "pending"
                else -> "pending"
            }

            id = companionsgroup.checkedChipId
            chipselected = companionsgroup.findViewById<Chip>(id)
            chiptextvalue = chipselected.text.toString()
            guest.companion = when (chiptextvalue) {
                "Adult" -> "adult"
                "Child" -> "child"
                "Baby" -> "baby"
                "None" -> "none"
                else -> "none"
            }
        }

        guest.editGuest()
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
                requestPermissions(permissions, Guest_EditDetail.PERMISSION_CODE)
            } else {
                //permission already granted
                startActivityForResult(intent, Guest_EditDetail.IMAGE_PICK_CODE)
            }
        } else {
            //system OS is < Marshmallow
            startActivityForResult(intent, Guest_EditDetail.IMAGE_PICK_CODE)
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        internal val PERMISSION_CODE = 1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == Guest_EditDetail.IMAGE_PICK_CODE) {
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
