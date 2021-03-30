package com.example.newevent2.MVP

import android.app.Activity
import android.view.View
import com.example.newevent2.LoginView
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.EventModel
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserModel
import com.example.newevent2.OnboardingView
import com.example.newevent2.saveLog
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import kotlinx.android.synthetic.main.event_edit.*

class OnboardingPresenter(
    view: OnboardingView,
    activity: Activity,
    user: User,
    event: Event
) {

    var viewOnboarding: OnboardingView = view

    init {
        onboardUser(activity, user, event)
    }

    private fun onboardUser(
        activity: Activity,
        user: User,
        event: Event
    ) {
        val userModel = UserModel(user.key)
        userModel.addUser(user, object : UserModel.FirebaseSaveSuccess {
            override fun onSaveSuccess(flag: Boolean) {
                if (flag) {
                    val eventModel = EventModel(activity)
                    eventModel.addEvent(event, null, object : EventModel.FirebaseSaveSuccess {
                        override fun onSaveSuccess(eventid: String) {
                            if (eventid != "") {
                                // Event was saved successfully and will be passed to the user so the session can be saved
                                user.eventid = eventid
                                // This is made to save the eventid to the user, although several other values will be overwritten.
                                // Hopefully this won't be a problem.
                                userModel.editUser(user)
                                // This info should be already loaded into the user so it can all be saved in the session
//                                UID
//                                Email
//                                Autentication
//                                Eventid
//                                Shortname
                                user.saveUserSession(activity)
                                viewOnboarding.onSuccess()
                            } else {
                                // There was an issue when saving the event
                                viewOnboarding.onError("EVENTERROR")
                            }
                        }
                    })
                } else {
                    viewOnboarding.onError("USERERROR")
                }
            }
        })
    }

    interface ViewOnboardingActivity {
        fun onSuccess()
        fun onError(errorcode: String) // A fin de que se pueda especificar en que parte del proceso se dio el error
    }
}