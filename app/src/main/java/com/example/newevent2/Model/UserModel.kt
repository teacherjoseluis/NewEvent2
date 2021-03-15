package com.example.newevent2.Model

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.newevent2.Functions.getCurrentDateTime
import com.example.newevent2.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UserModel(
    //This user creates and edits Users into Firebase
    val key: String
) {
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private val postRef = myRef.child("User").child(this.key)
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getUser(dataFetched: FirebaseSuccessListenerUser) {
        val firebaseUser = User(key)
        val userListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    firebaseUser.apply {
                        eventid = p0.child("eventid").getValue(String::class.java)!!
                        shortname = p0.child("shortname").getValue(String::class.java)!!
                        email = p0.child("email").getValue(String::class.java)!!
                        country = p0.child("country").getValue(String::class.java)!!
                        language = p0.child("language").getValue(String::class.java)!!
                        createdatetime = p0.child("createdatetime").getValue(String::class.java)!!
                        authtype = p0.child("authtype").getValue(String::class.java)!!
                        imageurl = p0.child("imageurl").getValue(String::class.java)!!
                        role = p0.child("role").getValue(String::class.java)!!
                        hasevent = p0.child("hasevent").getValue(String::class.java)!!
                        hastask = p0.child("hastask").getValue(String::class.java)!!
                        haspayment = p0.child("haspayment").getValue(String::class.java)!!
                        hasguest = p0.child("hasguest").getValue(String::class.java)!!
                        hasvendor = p0.child("hasvendor").getValue(String::class.java)!!
                    }
                }
                dataFetched.onUserexists(firebaseUser)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(userListenerActive)
    }

    fun editUser(user: User) {
        //Commented entries correspond to values in the User entity that are not to be edited

        postRef.child("eventid").setValue(user.eventid)
        postRef.child("shortname").setValue(user.shortname)
        //postRef.child("email").setValue(user.email)
        postRef.child("country").setValue(user.country)
        postRef.child("language").setValue(user.language)
        //postRef.child("createdatetime").setValue(user.createdatetime)
        //postRef.child("authtype").setValue(user.authtype)
        //postRef.child("imageurl").setValue(user.imageurl)
        postRef.child("role").setValue(user.role)
        //postRef.child("hasevent").setValue(user.hasevent)
        //postRef.child("hastask").setValue(user.hastask)
        //postRef.child("haspayment").setValue(user.haspayment)
        //postRef.child("hasguest").setValue(user.hasguest)
        //postRef.child("hasvendor").setValue(user.hasvendor)
    }

    fun editUserImage(uri: Uri, imageurl: String) {

        postRef.child("imageurl").setValue(imageurl)

        val imageRef = storageRef.child("images/User/$key/${uri.lastPathSegment}")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnFailureListener {
            return@addOnFailureListener
        }.addOnSuccessListener {
            return@addOnSuccessListener
        }
    }

    fun addUser(user: User) {
        val user = hashMapOf(
            "eventid" to user.eventid,
            "shortname" to user.shortname,
            "email" to user.email,
            "country" to user.country,
            "language" to user.language,
            "createdatetime" to getCurrentDateTime(),
            "authtype" to user.authtype,
            "role" to user.role,
            "hasevent" to user.hasevent,
            "status" to "A"
        )

        postRef.setValue(user as Map<String, Any>)
            .addOnFailureListener {
                return@addOnFailureListener
            }
            .addOnSuccessListener {
                return@addOnSuccessListener
            }
    }

    fun login(
        activity: Activity,
        authtype: String,
        UserEmail: String?,
        UserPassword: String?,
        credential: AuthCredential?
    ) {
        when (authtype) {
            "email" -> {
                mAuth.signInWithEmailAndPassword(UserEmail!!, UserPassword!!)
                if (mAuth.currentUser!!.isEmailVerified) {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.success_email_login),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.notverified_email_login),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            else -> {
                mAuth.signInWithCredential(credential!!)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.success_sn_login),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            try {
                                throw task.exception!!
                            } catch (e: Exception) {
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.failure_sn_login),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }


        }
    }

    fun logout(
        activity: Activity,
        authtype: String,
        mGoogleSignInClient: GoogleSignInClient?,
        mFacebookLoginManager: LoginManager?
    ) {
        when (authtype) {
            "google" -> {
                mGoogleSignInClient!!.signOut().addOnCompleteListener(activity) {
                }
            }
            "facebook" -> {
                mFacebookLoginManager!!.logOut()
            }
        }
        mAuth.signOut()
        Toast.makeText(activity, activity.getString(R.string.success_logout), Toast.LENGTH_SHORT)
            .show()
    }

    private fun verifyaccount(activity: Activity) {
        val user = mAuth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, activity.getString(R.string.success_account_verification), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, activity.getString(R.string.failed_account_verification), Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun sendpasswordreset(activity: Activity, userEmail: String) {
        mAuth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, activity.getString(R.string.success_password_reset_email), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.failed_password_reset_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    interface FirebaseSuccessListenerUser {
        fun onUserexists(user: User)
    }
}