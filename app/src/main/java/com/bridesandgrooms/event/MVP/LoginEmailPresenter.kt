package com.bridesandgrooms.event.MVP

import android.app.Activity
import com.bridesandgrooms.event.LoginEmailView

class LoginEmailPresenter(
    view: LoginEmailView,
    activity: Activity,
    UserEmail: String,
    UserPassword: String
) {

    //    init {
//        loginUser(activity, UserEmail, UserPassword)
//    }

//    private fun loginUser(
//        activity: Activity,
//        UserEmail: String,
//        UserPassword: String
//    ) {
//        val user = User()
//        user.login(
//            activity,
//            "email",
//            UserEmail,
//            UserPassword,
//            null,
//            object : User.FirebaseUserId {
//                override fun onUserId(userid: String, email: String) {
//                    if (userid != "") {
//                        val userModel = UserModel(userid)
//                        userModel.getUser(object : UserModel.FirebaseSuccessUser {
//                            override fun onUserexists(user: User) {
//                                if (user.createdatetime != "") {
//                                    //save user into local storage
//                                    user.saveUserSession(activity)
//                                    viewEmailLogin.onLoginEmailSuccess()
//                                } else {
//                                    viewEmailLogin.onOnboarding(userid, email, "email")
//                                }
//                            }
//                        })
//                    } else {
//                        viewEmailLogin.onLoginEmailError()
//                    }
//                }
//            })
//    }

    interface ViewEmailLoginActivity {
        fun onLoginEmailSuccess()
        fun onOnboarding(userid: String, email: String, authtype: String)
        fun onLoginEmailError()
    }
}