package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.VendorCreationException
import Application.VendorDeletionException
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.databinding.NewVendorBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class VendorCreateEdit : Fragment() {

    private lateinit var vendorItem: Vendor
    private lateinit var binding: NewVendorBinding
    private lateinit var user: User
    private lateinit var context: Context

    private val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus && view is TextInputEditText) {
            val parentLayout = view.parent.parent as? TextInputLayout
            parentLayout?.error = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        context = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = User().getUser(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.new_vendor, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.newvendor_title)

        val language = this.resources.configuration.locales.get(0).language
        val categorieslist = Category.getAllCategories(language)
        val adapter =
            ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, categorieslist)
        binding.categoryAutocomplete.setAdapter(adapter)

        vendorItem = arguments?.getParcelable<Vendor>("vendor") ?: Vendor()

        //Fields in the form are loaded in case there is a vendor passed as parameter
        if (vendorItem.key.isNotEmpty()) {
            binding.nameinputedit.setText(vendorItem.name)
            binding.phoneinputedit.setText(vendorItem.phone)
            binding.mailinputedit.setText(vendorItem.email)
            binding.categoryAutocomplete.setText(Category.getCategoryName(vendorItem.category), false)
        }
        binding.nameinputedit.onFocusChangeListener = focusChangeListener
        binding.phoneinputedit.onFocusChangeListener = focusChangeListener
        binding.mailinputedit.onFocusChangeListener = focusChangeListener

        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        binding.phoneinputedit.apply {
            addTextChangedListener(PhoneNumberFormattingTextWatcher())

            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    val formattedNumber = PhoneNumberUtils.formatNumber(
                        text.toString(),
                        tm.simCountryIso
                    )
                    // Check if formattedNumber is null
                    if (formattedNumber != null) {
                        text = Editable.Factory.getInstance().newEditable(formattedNumber)
                    }

                    // Clear the error from the parent TextInputLayout
                    val parentLayout = view.parent.parent as? TextInputLayout
                    parentLayout?.error = null
                }
            }
        }

        binding.categoryAutocomplete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.categoryAutocomplete.showDropDown()
            }
        })

        binding.button.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Add_Vendor")
            val isValid = validateAllInputs()
            if (isValid) {
                savevendor()
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //setting up the menu, if it's not a new vendor being created,
        // the user will be able to remove it
        if (vendorItem.key.isNotEmpty()) {
            inflater.inflate(R.menu.vendors_menu2, menu)
            menu.findItem(R.id.remove_vendor).isEnabled = true
            if (!binding.phoneinputedit.text.isNullOrBlank()) {
                menu.findItem(R.id.call_vendor).isEnabled = true
            }
            if (!binding.mailinputedit.text.isNullOrBlank()) {
                menu.findItem(R.id.email_vendor).isEnabled = true
            }
            if (vendorItem.placeid.isNotEmpty()) {
                menu.findItem(R.id.google_vendor).isEnabled = true
            }
        }
    }

    // if the vendor is added through google then this should be disabled
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Removing the Vendor
            R.id.remove_vendor -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Remove_Vendor")
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.title_delete))
                    .setMessage(getString(R.string.delete_confirmation))
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Vendor")
                        val paymentdb = PaymentDBHelper(context)
                        if (paymentdb.hasVendorPayments(vendorItem.key) == 0) {
                            try {
                                deleteVendor(context, user, vendorItem)
                                finish()
                            } catch (e: VendorDeletionException) {
                                AnalyticsManager.getInstance().trackError(
                                    SCREEN_NAME,
                                    e.message.toString(),
                                    "deleteVendor()",
                                    e.stackTraceToString()
                                )
                                Log.e(TAG, e.message.toString())
                            }
                        } else {
                            Snackbar.make(
                                binding.vendorLayout, getString(R.string.error_vendorwithpayments),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(R.string.see_payments) {
                                    val fragment = VendorsAll()
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, fragment)
                                        .commit()
                                }
                                .show()
                        }
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }

            R.id.call_vendor -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Call_Vendor")
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", binding.phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
                true
            }

            R.id.email_vendor -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Email_Vendor")
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, binding.mailinputedit.text.toString())
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.requestinfo))
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        getString(R.string.error_noemailclient),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }

            R.id.google_vendor -> {
                val uri = Uri.parse("https://www.google.com/maps/place/?q=${vendorItem.name}")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    val extras = Bundle().apply {
                        putBoolean("new_window", true)
                    }
                    putExtras(extras)
                }
                startActivity(intent)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun validateAllInputs(): Boolean {
        var isValid = true
        val validator = InputValidator(context)

        val nameValidation =
            validator.validate(binding.nameinputedit)
        if (!nameValidation) {
            binding.nameinput.error = validator.errorCode
            isValid = false
        }

        val phoneValidation =
            validator.validate(binding.phoneinputedit)
        if (!phoneValidation) {
            binding.phoneinput.error = validator.errorCode
            isValid = false
        }

        val mailValidation =
            validator.validate(binding.mailinputedit)
        if (!mailValidation) {
            binding.mailinput.error = validator.errorCode
            isValid = false
        }

        val spinnerValidation = validator.validateSpinner(binding.categoryAutocomplete.toString())
        if (!spinnerValidation) {
            binding.categoryAutocomplete.error = validator.errorCode
            isValid = false
        }
        return isValid
    }

    private fun savevendor() {
        binding.nameinputedit.isEnabled = false
        binding.phoneinputedit.isEnabled = false
        binding.mailinputedit.isEnabled = false
        binding.categoryspinner.isEnabled = false
        binding.button.isEnabled = false

        vendorItem.apply {
            name = binding.nameinputedit.text.toString()
            phone = binding.phoneinputedit.text.toString()
            email = binding.mailinputedit.text.toString()
            category = Category.getCategoryCode(binding.categoryAutocomplete.text.toString())
        }

        if (vendorItem.key.isEmpty()) {
            try {
                addVendor(context, user, vendorItem)
            } catch (e: VendorCreationException) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "addVendor()",
                    e.stackTraceToString()
                )
                Log.e(TAG, e.message.toString())
            }
        } else {
            try {
                editVendor(context, user, vendorItem)
            } catch (e: VendorCreationException) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "editVendor()",
                    e.stackTraceToString()
                )
                Log.e(TAG, e.message.toString())
            }
        }
        finish()
    }

    fun finish() {
        val fragment = VendorsAll()
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }, 500)
    }

    companion object {
        //Permission code
        internal const val PERMISSION_CODE = 1001
        const val SCREEN_NAME = "Vendor CreateEdit"
        const val TAG = "VendorCreateEdit"
    }
}