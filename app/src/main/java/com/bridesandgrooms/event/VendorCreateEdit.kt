package com.bridesandgrooms.event

import NetworkUtils
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.databinding.NewVendorBinding
import com.bridesandgrooms.event.UI.TextValidate
import com.google.android.material.textfield.TextInputEditText
//import kotlinx.android.synthetic.main.new_guest.*
//import kotlinx.android.synthetic.main.new_vendor.*
//import kotlinx.android.synthetic.main.new_vendor.binding.button
//import kotlinx.android.synthetic.main.new_vendor.binding.mailimage
//import kotlinx.android.synthetic.main.new_vendor.binding.mailinputedit
//import kotlinx.android.synthetic.main.new_vendor.binding.nameinputedit
//import kotlinx.android.synthetic.main.new_vendor.binding.phoneimage
//import kotlinx.android.synthetic.main.new_vendor.binding.phoneinputedit
//import kotlinx.android.synthetic.main.vendor_binding.googlecard.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    private lateinit var binding: NewVendorBinding

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.new_vendor)

        // Declaring and enabling the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Adding the title
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.newvendor_title)

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)

        val language = this.resources.configuration.locales.get(0).language
        val categorieslist = Category.getAllCategories(language)
        categorieslist.add(0, getString(R.string.selectcategory))
        val adapteractv =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorieslist)
        binding.categoryspinner.adapter = adapteractv

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
            binding.nameinputedit.setText(vendoritem.name)
            binding.phoneinputedit.setText(vendoritem.phone)
            binding.mailinputedit.setText(vendoritem.email)

            if (vendoritem.category != "") {
                binding.categoryspinner.setSelection(categorieslist.indexOf(vendoritem.category))
            } else {
                binding.categoryspinner.setSelection(0)
            }

            val paymentDB = PaymentDBHelper(this)
            val paymentlist = paymentDB.getVendorPaymentList(vendoritem.key)!!

            if (paymentlist.size != 0) {
                binding.cardtitle.visibility = ConstraintLayout.VISIBLE
                binding.cardtitle.text = getString(R.string.payment)

                val recyclerView = binding.PaymentsRecyclerView
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context).apply {
                        stackFromEnd = true
                        reverseLayout = true
                    }
                }
                val rvAdapter = Rv_PaymentAdapter2(lifecycleScope, paymentlist)
                recyclerView.adapter = rvAdapter
            }

            if (vendoritem.googlevendorname != "") {
                binding.googlecard.root.visibility = ConstraintLayout.VISIBLE
                binding.googlecard.googlevendorname.text = vendoritem.googlevendorname
                binding.googlecard.ratingnumber.text = vendoritem.ratingnumber.toString()
                binding.googlecard.reviews.text =
                    "(" + vendoritem.reviews.toString() + ")"
                binding.googlecard.rating.rating = vendoritem.rating.toFloat()
                binding.googlecard.location.text = vendoritem.location
                binding.googlecard.root.setOnClickListener {
                    val uris =
                        Uri.parse("https://www.google.com/maps/place/?q=" + vendoritem.googlevendorname)
                    val intents = Intent(Intent.ACTION_VIEW, uris)
                    val b = Bundle()
                    b.putBoolean("new_window", true)
                    intents.putExtras(b)
                    startActivity(intents)
                }
                getImgfromPlaces(
                    this,
                    vendoritem.placeid,
                    resources.getString(R.string.google_maps_key),
                    vendoritem.googlevendorname,
                    binding.googlecard.placesimagevendor
                )
            }
        }

        //This is to validate the vendor's name after the focus shifts somewhere else
        binding.nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(binding.nameinputedit).namefieldValidate()
                if (validationmessage != "") {
                    binding.nameinputedit.error = "Error in Vendor name: $validationmessage"
                }
            }
        }

        //Removes any error by default whenever the field is clicked into
        binding.nameinputedit.setOnClickListener {
            binding.nameinputedit.error = null
        }

        //Loads the activity that allows to search in Google Places for a Vendor
        binding.googleicon.setOnClickListener {
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocompletePlaceCode)
        }
        binding.googleicon.tooltipText = getString(R.string.googlesearch)

        binding.phoneinputedit.setOnClickListener {
            binding.phoneinputedit.error = null
        }

        //This validates the input on the phone field so the user enters a valid number
        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        mPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mPhoneNumber.setOnFocusChangeListener { _, _ ->
            PhoneNumberUtils.formatNumber(
                mPhoneNumber.text.toString(),
                tm.simCountryIso
            )
        }

        //In case there is already a phone entered, this allows the user to dial
        // it from this view
        binding.phoneimage.setOnClickListener {
            if (!binding.phoneinputedit.text.isNullOrBlank()) {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", binding.phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
            }
        }

        //In case there is already an email entered, this allows the user to dial
        // it from this view
        binding.mailimage.setOnClickListener {
            if (!binding.mailinputedit.text.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, binding.mailinputedit.text.toString())
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

        binding.button.setOnClickListener {
            //No blanks validation before saving
            var inputvalflag = true
            if (binding.nameinputedit.text.toString().isEmpty()) {
                binding.nameinputedit.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            }
            if (binding.phoneinputedit.text.toString().isEmpty()) {
                binding.phoneinputedit.error = getString(R.string.error_vendorphoneinput)
                inputvalflag = false
            }
            if (binding.categoryspinner.selectedItem.toString() == getString(R.string.selectcategory)) {
                Toast.makeText(this, getString(R.string.selectcategory), Toast.LENGTH_SHORT)
                    .show()
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
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        val paymentdb = PaymentDBHelper(this)
                        // Let's make sure that there are no payments associated to the vendor
                        // in order to proceed
//                        if (!PermissionUtils.checkPermissions(applicationContext)) {
//                            PermissionUtils.alertBox(this)
//                        } else {
                            if (paymentdb.hasVendorPayments(vendoritem.key) == 0) {
                                lifecycleScope.launch {
                                    deleteVendor(this@VendorCreateEdit, vendoritem)
                                }
                                Thread.sleep(1500)
                                finish()
//                            finish()
                            } else {
                                //If there are payments associated we will not be able to delete the vendor
                                Toast.makeText(
                                    this,
                                    getString(R.string.error_vendorwithpayments),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
//                        }
                    }
                    // A null listener allows the binding.button to dismiss the dialog and take no
                    // further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                //true
                //Disable all controls in the view
//                binding.nameinputedit.isEnabled = false
//                binding.phoneinputedit.isEnabled = false
//                binding.mailinputedit.isEnabled = false
//                binding.button.isEnabled = false
//                super.onOptionsItemSelected(item)
                true
            }
            else -> {
                //true
                super.onOptionsItemSelected(item)
            }
        }
    }

    private suspend fun disableControls() {
        binding.nameinputedit.isEnabled = false
        binding.phoneinputedit.isEnabled = false
        binding.mailinputedit.isEnabled = false
        binding.button.isEnabled = false

        setResult(Activity.RESULT_OK, Intent())
        delay(1500) // Replace Thread.sleep with delay from coroutines
        finish()
    }

    private fun savevendor() {
        //Loading view is loaded while saving the vendor.
        // I guess this is happening to allow asynchronous processes to complete
        loadingview.visibility = ConstraintLayout.VISIBLE
        withdataview.visibility = ConstraintLayout.GONE

        binding.nameinputedit.isEnabled = false
        binding.phoneinputedit.isEnabled = false
        binding.mailinputedit.isEnabled = false
        binding.categoryspinner.isEnabled = false
        binding.button.isEnabled = false

        vendoritem.name = binding.nameinputedit.text.toString()
        vendoritem.phone = binding.phoneinputedit.text.toString()
        vendoritem.email = binding.mailinputedit.text.toString()
        vendoritem.category = binding.categoryspinner.selectedItem.toString()

        //Having vendoritem populated allows to decide whether we are adding a new
        // or editing an existing vendor
//        if (!PermissionUtils.checkPermissions(applicationContext)) {
//            PermissionUtils.alertBox(this)
//        } else {
            val networkUtils = NetworkUtils(this)
            if (networkUtils.isNetworkAvailable()) {
                if (vendoritem.key == "") {
                    addVendor(this, vendoritem, CALLER)
                } else if (vendoritem.key != "") {
                    editVendor(this, vendoritem)
                }
            } else {
                displayErrorMsg(this, getString(R.string.error_networkconnection))
                loadingview.visibility = ConstraintLayout.GONE
                withdataview.visibility = ConstraintLayout.VISIBLE
                binding.nameinputedit.isEnabled = true
                binding.phoneinputedit.isEnabled = true
                binding.mailinputedit.isEnabled = true
                binding.categoryspinner.isEnabled = true
                binding.button.isEnabled = true
            }
            Thread.sleep(2000)
            finish()
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {

            //Populating name and phone fields with whatever comes from Google as a Vendor is selected
            binding.nameinputedit.setText(data?.getStringExtra("place_name"))
            binding.phoneinputedit.setText(data?.getStringExtra("place_phone"))

            // There is a card being displayed with the details of the Vendor.
            // From what I'm getting, this is only visible when a Vendor is added but not when the user returns
            binding.googlecard.root.visibility = ConstraintLayout.VISIBLE
            binding.googlecard.googlevendorname.text = data?.getStringExtra("place_name")
            binding.googlecard.ratingnumber.text = data?.getDoubleExtra("place_rating", 0.0).toString()
            binding.googlecard.reviews.text =
                "(" + data?.getIntExtra("place_userrating", 0).toString() + ")"
            binding.googlecard.rating.rating = data?.getDoubleExtra("place_rating", 0.0)!!.toFloat()
            binding.googlecard.location.text = data.getStringExtra("place_address")

            placelatitude = data.getDoubleExtra("place_latitude", 0.0)
            placelongitude = data.getDoubleExtra("place_longitude", 0.0)
            placelocation = data.getStringExtra("place_name")!!

            vendoritem.placeid = data.getStringExtra("place_id")!!
            vendoritem.googlevendorname = data.getStringExtra("place_name")!!
            vendoritem.rating = data.getDoubleExtra("place_rating", 0.0).toFloat().toString()
            vendoritem.ratingnumber = data.getDoubleExtra("place_rating", 0.0).toFloat()
            vendoritem.reviews = data.getIntExtra("place_userrating", 0).toFloat()

            //The card allows to click on it and go to Google Maps
            binding.googlecard.root.setOnClickListener {
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
        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)

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

