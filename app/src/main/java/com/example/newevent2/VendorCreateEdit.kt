package com.example.newevent2

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.newevent2.Functions.*
import com.example.newevent2.MVP.ImagePresenter
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.PaymentDBHelper
import com.example.newevent2.Model.Vendor
import com.example.newevent2.Model.VendorModel
import com.example.newevent2.ui.TextValidate
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.event_edit.*
import kotlinx.android.synthetic.main.new_vendor.*
import kotlinx.android.synthetic.main.new_vendor.button
import kotlinx.android.synthetic.main.summary_weddinglocation.view.*
import kotlinx.android.synthetic.main.vendor_googlecard.view.*

class VendorCreateEdit : AppCompatActivity(), CoRAddEditVendor {

    private var uri: Uri? = null
    private var chiptextvalue: String? = null
    private var categoryrsvp: String = ""
    private var categorycompanions: String = ""
    private val autocompletePlaceCode = 1

    private lateinit var vendoritem: Vendor
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    lateinit var mContext: Context
    private lateinit var activitymenu: Menu

    var placelatitude = 0.0
    var placelongitude = 0.0
    var placelocation = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_vendor)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "New Vendor"

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)


        val extras = intent.extras
        vendoritem = if (extras!!.containsKey("vendor")) {
            intent.getParcelableExtra("vendor")!!
        } else {
            Vendor()
        }

        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (vendoritem.key != "") {
            nameinputedit.setText(vendoritem.name)
            phoneinputedit.setText(vendoritem.phone)
            mailinputedit.setText(vendoritem.email)
        }

        nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(nameinputedit).namefieldValidate()
                if (validationmessage != "") {
                    nameinputedit.error = "Error in Vendor name: $validationmessage"
                }
            }
        }

        nameinputedit.setOnClickListener {
            nameinputedit.error = null
        }

        googleicon.setOnClickListener {
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocompletePlaceCode)
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

        phoneimage.setOnClickListener {
            if (!phoneinputedit.text.isNullOrBlank()) {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
            }
        }

        mailimage.setOnClickListener {
            if (!mailinputedit.text.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, mailinputedit.text.toString())
                    intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback")
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        this,
                        "There are no email client installed on your device.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (nameinputedit.text.toString().isEmpty()) {
                nameinputedit.error = "Vendor name is required!"
                inputvalflag = false
            }
            if (phoneinputedit.text.toString().isEmpty()) {
                phoneinputedit.error = "Vendor phone is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                savevendor()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (vendoritem.key != "") {
            menuInflater.inflate(R.menu.vendors_menu2, menu)
            activitymenu = menu
            val guestmenu = activitymenu.findItem(R.id.remove_guest)
            guestmenu.isEnabled = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.remove_guest -> {
                AlertDialog.Builder(this)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            val paymentdb = PaymentDBHelper(this)
                            if (paymentdb.hasVendorPayments(vendoritem.key) == 0) {
                                deleteVendor(this, vendoritem)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Vendor can't be removed as there are still payments associated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }
            else -> {
                true
            }
        }
    }

    private fun savevendor() {
        loadingview.visibility = ConstraintLayout.VISIBLE
        withdataview.visibility = ConstraintLayout.GONE

        vendoritem.name = nameinputedit.text.toString()
        vendoritem.phone = phoneinputedit.text.toString()
        vendoritem.email = mailinputedit.text.toString()

        if (vendoritem.key == "") {
            addVendor(this, vendoritem, CALLER)
        } else if (vendoritem.key != "") {
            editVendor(this, vendoritem)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {

            nameinputedit.setText(data?.getStringExtra("place_name"))
            phoneinputedit.setText(data?.getStringExtra("place_phone"))

            googlecard.visibility = ConstraintLayout.VISIBLE

            googlecard.googlevendorname.text = data?.getStringExtra("place_name")
            googlecard.ratingnumber.text = data?.getDoubleExtra("place_rating", 0.0).toString()
            googlecard.reviews.text =
                "(" + data?.getIntExtra("place_userrating", 0).toString() + ")"
            googlecard.rating.rating = data?.getDoubleExtra("place_rating", 0.0)!!.toFloat()
            googlecard.location.text = data?.getStringExtra("place_address")

            placelatitude = data.getDoubleExtra("place_latitude", 0.0)
            placelongitude = data.getDoubleExtra("place_longitude", 0.0)
            placelocation = data.getStringExtra("place_name")!!

            googlecard.setOnClickListener {
                val gmmIntentUri =
                    Uri.parse("geo:${placelatitude},${placelongitude}?z=15&q=${placelocation}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(this.packageManager)?.let {
                    startActivity(mapIntent)
                }
            }
        }
    }

    companion object {
        //Permission code
        internal val PERMISSION_CODE = 1001
        const val CALLER = "vendor"
        const val VENDORIMAGE = "vendorimage"
    }

    override fun onAddEditVendor(vendor: Vendor) {
        (mContext as VendorCreateEdit).loadingview.visibility = ConstraintLayout.GONE
        (mContext as VendorCreateEdit).withdataview.visibility = ConstraintLayout.VISIBLE
        VendorsAll.vendorcreated_flag = 1
    }
}
