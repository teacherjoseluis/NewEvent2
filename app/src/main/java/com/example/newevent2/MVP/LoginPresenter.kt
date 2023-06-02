package com.example.newevent2.MVP

import android.app.Activity
import com.example.newevent2.LoginView
import com.google.firebase.auth.AuthCredential

class LoginPresenter(
    view: LoginView,
    activity: Activity,
    authtype: String,
    username: String?,
    password: String?,
    credential: AuthCredential?
) {

    //    private fun loginUser(
//        activity: Activity,
//        authtype: String,
//        username: String?,
//        password: String?,
//        credential: AuthCredential?
//    ) {
//        val user = User()
//        user.login(
//            activity,
//            authtype,
//            username,
//            password,
//            credential,
//            object : User.FirebaseUserId {
//                override fun onUserId(userid: String, email: String) {
//                    if (userid != "") {
//                        val userModel = UserModel(userid)
//                        userModel.getUser(object : UserModel.FirebaseSuccessUser {
//                            override fun onUserexists(user: User) {
//                                if (user.hasevent == "Y") {
//                                    //save user into local storage
//                                    user.saveUserSession(activity)
//                                    viewLogin.onLoginSuccess()
//                                } else {
//                                    viewLogin.onOnboarding(userid, email, authtype)
//                                }
//                            }
//                        })
//                    } else {
//                        viewLogin.onLoginError()
//                    }
//                }
//            })
//    }

    interface ViewLoginActivity {
        fun onLoginSuccess(email: String)
        fun onOnboarding(userid: String, email: String, authtype: String)
        fun onLoginError()
    }
}