package com.example.newevent2.MVP

import android.app.Activity
import android.view.View
import com.example.newevent2.LoginView
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential

class LoginPresenter(
    view: LoginView,
    activity: Activity,
    authtype: String,
    credential: AuthCredential
) {

    var viewLogin: LoginView = view

    init {
        loginUser(activity, authtype, credential)
    }

    private fun loginUser(
        activity: Activity,
        authtype: String,
        credential: AuthCredential
    ) {
        val user = User()
        user.login(
            activity,
            authtype,
            null,
            null,
            credential,
            object : User.FirebaseUserId {
                override fun onUserId(userid: String) {
                    if (userid != "") {
                        val userModel = UserModel(userid)
                        userModel.getUser(object : UserModel.FirebaseSuccessUser {
                            override fun onUserexists(user: User) {
                                if (user.createdatetime != "") {
                                    //save user into local storage
                                    user.saveUserSession(activity)
                                    viewLogin.onSuccess()
                                } else {
                                    viewLogin.onOnboarding(userid)
                                }
                            }
                        })
                    } else {
                        viewLogin.onLoginError()
                    }
                }
            })
    }

    interface ViewLoginActivity {
        fun onSuccess()
        fun onOnboarding(userid: String)
        fun onLoginError()
    }
}