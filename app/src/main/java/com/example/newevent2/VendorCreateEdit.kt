package com.example.newevent2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
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
import com.example.newevent2.Model.PaymentDBHelper
import com.example.newevent2.Model.Vendor
import com.example.newevent2.ui.TextValidate
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.new_vendor.*
import kotlinx.android.synthetic.main.new_vendor.button
import kotlinx.android.synthetic.main.vendor_googlecard.view.*

class VendorCreateEdit : AppCompatActivity(), CoRAddEditVendor {

    private val autocompletePlaceCode = 1

    private lateinit var vendoritem: Vendor
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    lateinit var mContext: Context
    private lateinit var activitymenu: Menu

    private var placelatitude = 0.0
    private var placelongitude = 0.0
    private var placelocation = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_vendor)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Adding the title
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.newvendor_title)

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)

        //Sets vendoritem blank if nothing comes from the extras
        // otherwise it assumes a vendor item has been passed as a parameter
        val extras = intent.extras
        vendoritem = if (extras!!.containsKey("vendor")) {
            intent.getParcelableExtra("vendor")!!
        } else {
            Vendor()
        }

        //Fields in the form are loaded in case there is a vendor passed as parameter
        if (vendoritem.key != "") {
            nameinputedit.setText(vendoritem.name)
            phoneinputedit.setText(vendoritem.phone)
            mailinputedit.setText(vendoritem.email)
        }

        //This is to validate the vendor's name after the focus shifts somewhere else
        nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(nameinputedit).namefieldValidate()
                if (validationmessage != "") {
                    nameinputedit.error = "Error in Vendor name: $validationmessage"
                }
            }
        }

        //Removes any error by default whenever the field is clicked into
        nameinputedit.setOnClickListener {
            nameinputedit.error = null
        }

        //Loads the activity that allows to search in Google Places for a Vendor
        googleicon.setOnClickListener {
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocompletePlaceCode)
        }

        phoneinputedit.setOnClickListener {
            phoneinputedit.error = null
        }

        //This validates the input on the phone field so the user enters a valid number
        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mPhoneNumber.setOnFocusChangeListener { _, _ ->
            PhoneNumberUtils.formatNumber(
                mPhoneNumber.text.toString(),
                tm.simCountryIso
            )
        }

        //In case there is already a phone entered, this allows the user to dial
        // it from this view
        phoneimage.setOnClickListener {
            if (!phoneinputedit.text.isNullOrBlank()) {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
            }
        }

        //In case there is already an email entered, this allows the user to dial
        // it from this view
        mailimage.setOnClickListener {
            if (!mailinputedit.text.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, mailinputedit.text.toString())
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.requestinfo))
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        this,
                        getString(R.string.error_noemailclient),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        button.setOnClickListener {
            //No blanks validation before saving
            var inputvalflag = true
            if (nameinputedit.text.toString().isEmpty()) {
                nameinputedit.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            }
            if (phoneinputedit.text.toString().isEmpty()) {
                phoneinputedit.error = getString(R.string.error_vendorphoneinput)
                inputvalflag = false
            }
            if (inputvalflag) {
                savevendor()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //setting up the menu, if it's not a new vendor being created,
        // the user will be able to remove it
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
            // Removing the Vendor
            R.id.remove_guest -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_delete))
                    .setMessage(getString(R.string.delete_confirmation))
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes
                    ) { _, _ ->
                        val paymentdb = PaymentDBHelper(this)
                        // Let's make sure that there are no payments associated to the vendor
                        // in order to proceed
                        if (paymentdb.hasVendorPayments(vendoritem.key) == 0) {
                            deleteVendor(this, vendoritem)
                            finish()
                        } else {
                            //If there are payments associated we will not be able to delete the vendor
                            Toast.makeText(
                                this,
                                getString(R.string.error_vendorwithpayments),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    // A null listener allows the button to dismiss the dialog and take no
                    // further action.
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
        //Loading view is loaded while saving the vendor.
        // I guess this is happening to allow asynchronous processes to complete
        loadingview.visibility = ConstraintLayout.VISIBLE
        withdataview.visibility = ConstraintLayout.GONE

        vendoritem.name = nameinputedit.text.toString()
        vendoritem.phone = phoneinputedit.text.toString()
        vendoritem.email = mailinputedit.text.toString()

        //Having vendoritem populated allows to decide whether we are adding a new
        // or editing an existing vendor
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

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {

            //Populating name and phone fields with whatever comes from Google as a Vendor is selected
            nameinputedit.setText(data?.getStringExtra("place_name"))
            phoneinputedit.setText(data?.getStringExtra("place_phone"))

            // There is a card being displayed with the details of the Vendor.
            // From what I'm getting, this is only visible when a Vendor is added but not when the user returns
            googlecard.visibility = ConstraintLayout.VISIBLE
            googlecard.googlevendorname.text = data?.getStringExtra("place_name")
            googlecard.ratingnumber.text = data?.getDoubleExtra("place_rating", 0.0).toString()
            googlecard.reviews.text =
                "(" + data?.getIntExtra("place_userrating", 0).toString() + ")"
            googlecard.rating.rating = data?.getDoubleExtra("place_rating", 0.0)!!.toFloat()
            googlecard.location.text = data.getStringExtra("place_address")

            placelatitude = data.getDoubleExtra("place_latitude", 0.0)
            placelongitude = data.getDoubleExtra("place_longitude", 0.0)
            placelocation = data.getStringExtra("place_name")!!

            //The card allows to click on it and go to Google Maps
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

    //This is a callback when the Vendor has been added or edited. The idea is to shut off
    // the loading view when the confirmation that the async process is completed comes
    override fun onAddEditVendor(vendor: Vendor) {
        (mContext as VendorCreateEdit).loadingview.visibility = ConstraintLayout.GONE
        (mContext as VendorCreateEdit).withdataview.visibility = ConstraintLayout.VISIBLE
        VendorsAll.vendorcreated_flag = 1
    }

    companion object {
        //Permission code
        internal const val PERMISSION_CODE = 1001
        const val CALLER = "vendor"
    }
}

