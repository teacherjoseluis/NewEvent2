package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
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
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.new_guest.*
import kotlinx.android.synthetic.main.new_guest.phoneimage
import kotlinx.android.synthetic.main.new_guest.phoneinputedit
import kotlinx.android.synthetic.main.vendor_editdetail.*
import java.lang.IndexOutOfBoundsException
import java.util.*

class Vendor_EditDetail : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var uri: Uri
    private var localflag = false
    var googleMap: GoogleMap? = null
    var latLng: LatLngBounds? = null
    lateinit var vendorLocation: LatLng
    var vendorname: String = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vendor_editdetail)
        val intent = intent

        val vendoritem = VendorEntity().apply {
            contactid = intent.getStringExtra("contactid").toString()
            latitude = intent.getDoubleExtra("latitude", 0.0)
            longitude = intent.getDoubleExtra("longitude", 0.0)
            //key = intent.getStringExtra("key").toString()
            name = intent.getStringExtra("name").toString()
//            imageurl = intent.getStringExtra("imageurl").toString()
//            uri = Uri.parse(intent.getStringExtra("imageurl").toString())
            phone = intent.getStringExtra("phone").toString()
        }

        var contactname: String? = null
        var contactphoto: String? = null
        var contactphone: String? = null
//        var contactemail: String? = null
        var cursor: Cursor? = null
        var phonecursor: Cursor? = null
//            var emailcursor: Cursor? = null

        if (vendoritem.contactid == "local" || vendoritem.contactid == "google") localflag = true

        val whereclause = StringBuffer()
        whereclause.append(ContactsContract.Contacts._ID)
        whereclause.append(" = ")
        whereclause.append(vendoritem.contactid)

        val whereclausephone = StringBuffer()
        whereclausephone.append(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        whereclausephone.append(" = ")
        whereclausephone.append(vendoritem.contactid)

        if (!localflag) {
            cursor =
                contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    whereclause.toString(),
                    null, null
                )

            phonecursor =
                contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    whereclausephone.toString(),
                    null, null
                )

            cursor?.moveToNext()
            phonecursor?.moveToNext()

            contactname =
                cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                    .toString()
            contactphoto =
                cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))

            contactphone =
                phonecursor?.getString(phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .toString()

            cursor?.let { cursor.close() }
            phonecursor?.let { phonecursor.close() }
        }

        val phoneinput = findViewById<TextInputEditText>(R.id.phoneinputedit).also {
            when (vendoritem.contactid) {
                "local" -> it.isEnabled = true
                else -> it.isEnabled = false
            }
        }
        phoneinput.setText(contactphone?.let { it } ?: vendoritem.phone)

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val avatar = findViewById<ImageView>(R.id.contactavatar)
        avatar.setImageURI(contactphoto?.let { it.toUri() } ?:
        //vendoritem.imageurl.toUri()
        Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + resources.getResourcePackageName(R.drawable.avatar2)
                    + '/' + resources.getResourceTypeName(R.drawable.avatar2) + '/' + resources.getResourceEntryName(
                R.drawable.avatar2
            )
        )
        )

        val nameinput = findViewById<TextInputEditText>(R.id.nameinputedit).also {
            when (vendoritem.contactid) {
                "local" -> it.isEnabled = true
                else -> it.isEnabled = false
            }
        }
        nameinput.setText(contactname?.let { it } ?: vendoritem.name)

        if (vendoritem.contactid == "google") {
            vendorname = vendoritem.name

            vendorLocation = LatLng(vendoritem.latitude, vendoritem.longitude)
            latLng = LatLngBounds(
                LatLng(vendoritem.latitude - 5, vendoritem.longitude - 5),
                LatLng(vendoritem.latitude + 5, vendoritem.longitude + 5)
            )

            val mapview = findViewById<MapView>(R.id.mapView)
            // Show Map
            mapview.onCreate(savedInstanceState)
            mapview.onResume()
            mapview.getMapAsync(this)

            mapView.setOnClickListener(object : View.OnClickListener {
                val uri = String.format(
                    Locale.ENGLISH,
                    "geo:%f,%f",
                    vendoritem.latitude,
                    vendoritem.longitude
                )
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                override fun onClick(p0: View?) {
                    startActivity(intent)
                }
                //context!!.startActivity(intent)
            })
        } else {
            vendormap.visibility = View.INVISIBLE
        }

        if (vendoritem.contactid == "local") {

            nameinput.setOnClickListener {
                nameinput.error = null
            }

            phoneinput.setOnClickListener {
                phoneinput.error = null
            }

            val button = findViewById<Button>(R.id.button)
            button.visibility = View.VISIBLE

            button.setOnClickListener {
                var inputvalflag = true
                if (nameinput.text.toString().isEmpty()) {
                    nameinput.error = "Vendor name is required!"
                    inputvalflag = false
                }
                if (phoneinput.text.toString().isEmpty()) {
                    phoneinput.error = "Phone Number is required!"
                    inputvalflag = false
                }
                if (inputvalflag) {
                    val vendor = VendorEntity().apply {
                        name = nameinput.text.toString()
                        phone = phoneinput.text.toString()
                        contactid = "local"
                    }
                    vendor.addVendor()
                    finish()
                }
            }
        }

        phoneinput.setOnFocusChangeListener { p0, p1 ->
            PhoneNumberUtils.formatNumber(
                phoneinput.text.toString(),
                tm.simCountryIso
            )
        }

        phoneimage.setOnClickListener {
            if (!phoneinput.text.isNullOrBlank()) {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
            }
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        internal val PERMISSION_CODE = 1001
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0!!
        googleMap!!.addMarker(
            MarkerOptions().position(vendorLocation).title(vendorname)
        )
        googleMap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng!!.center,
                16f
            )
        )
        googleMap!!.uiSettings.setAllGesturesEnabled(false)
    }

}