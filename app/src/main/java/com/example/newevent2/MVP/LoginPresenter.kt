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
    UserEmail: String?,
    UserPassword: String?,
    credential: AuthCredential?
) {

    var viewLogin: LoginView = view

    init {
        loginUser(activity, authtype, UserEmail, UserPassword, credential)
    }

    private fun loginUser(
        activity: Activity,
        authtype: String,
        UserEmail: String?,
        UserPassword: String?,
        credential: AuthCredential?
    ) {
        val user = User()
        user.login(
            activity,
            authtype,
            UserEmail,
            UserPassword,
            credential,
            object : User.FirebaseUserId {
                override fun onUserId(userid: String) {
                    if (userid != "") {
                        val userModel = UserModel(userid)
                        userModel.getUser(object : UserModel.FirebaseSuccessUser {
                            override fun onUserexists(user: User) {
                                if (user.createdatetime != "") {
                                    //TODO salvar en el local storage
                                    //save user into local storage
                                    viewLogin.onSuccess()
                                } else {
                                    viewLogin.onOnboarding()
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
        fun onOnboarding()
        fun onLoginError()
    }
}