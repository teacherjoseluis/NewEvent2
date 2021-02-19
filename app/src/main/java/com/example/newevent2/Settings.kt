package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.etname
import kotlinx.android.synthetic.main.activity_main.saveImageActionButton
import kotlinx.android.synthetic.main.event_edit.*
import kotlinx.android.synthetic.main.eventdetail_event.view.*
import kotlinx.android.synthetic.main.settings.*

class Settings : AppCompatActivity() {

    private var uri: Uri? = null
    private var userSession = User()
    lateinit var storage: FirebaseStorage
    var userEntity = UserEntity()

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Settings"

        val usersessionlist = getUserSession(this)
        //val userEntity = UserEntity()
        userEntity.key = usersessionlist[0]

        userEntity.getUser(object : FirebaseSuccessListenerUser {
            override fun onUserexists(user: User) {
                uri = Uri.parse(user.imageurl)

                // Load thumbnail
                var storageRef: Any
                if (user.imageurl != "") {
                    userEntity.imageurl = user.imageurl
                    storage = FirebaseStorage.getInstance()
                    storageRef =
                        storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/User/${userEntity.key}/${userEntity.imageurl}")
                } else {
                    storageRef =
                        Uri.parse(
                            ContentResolver.SCHEME_ANDROID_RESOURCE +
                                    "://" + resources.getResourcePackageName(R.drawable.avatar2)
                                    + '/' + resources.getResourceTypeName(R.drawable.avatar2) + '/' + resources.getResourceEntryName(
                                R.drawable.avatar2
                            )
                        ).toString()
                }
                Glide.with(applicationContext)
                    .load(storageRef)
                    .centerCrop()
                    .into(imageView16)

                textinput.setText(user.shortname)
                val position = when(user.role) {
                    "Bride" -> 0
                    "Groom" -> 1
                    else -> 0
                }
                spinner.setSelection(position)
            }
        })


        saveImageActionButton.setOnClickListener()
        {
            showImagePickerDialog()
        }

        textinput.setOnClickListener()
        {
            textinput.error = null
        }

        textView27.setOnClickListener {
            logOff()
        }

        settingsbutton.setOnClickListener()
        {
            var inputvalflag = true
            if (textinput.text.toString().isEmpty()) {
                textinput.error = "Name is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                savesettings()
            }
        }
    }

    private fun savesettings() {
        val edituser = UserEntity().apply {
            shortname = textinput.text.toString()
            role = spinner.selectedItem.toString()
            //country = etPlannedDate.text.toString()
            //language = etPlannedTime.text.toString()
            imageurl = userEntity.imageurl
                //userEntity.imageurl
        }
        edituser.editUser(this, uri).also {
//            var userlocalsession =
//                this.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
//            val sessionEditor = userlocalsession!!.edit()
//            sessionEditor.putString("Shortname", edituser!!.shortname) // UID from Firebase
//            sessionEditor.putString("Role", edituser!!.role)
//            sessionEditor.putString("Imageurl", edituser!!.imageurl)
//            sessionEditor.apply()
            onBackPressed()
        }
    }

    private fun logOff(){
        mAuth.signOut()
        getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit().clear().commit()
        onBackPressed()

//        when (authtype) {
//            "google" -> {
//                mGoogleSignInClient!!.signOut().addOnCompleteListener(activity) {
//                    Log.i("Google Login", "Successful Logout: $UserEmail")
//                    logoutresult = true
//                }
//            }
//            "facebook" -> {
//                mFacebookLoginManager.logOut()
//                logoutresult = true
//            }
//        }
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
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                startActivityForResult(intent, IMAGE_PICK_CODE)
            }
        } else {
            //system OS is < Marshmallow
            startActivityForResult(intent, IMAGE_PICK_CODE)
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
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
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
                Glide.with(this)
                    .load(uri.toString())
                    .centerCrop()
                    .into(imageView16)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}