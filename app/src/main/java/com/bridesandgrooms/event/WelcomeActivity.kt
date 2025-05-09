package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.UserRetrievalException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.LoginView.Companion
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.databinding.WelcomeUserBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: WelcomeUserBinding
    private lateinit var userSession: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this@WelcomeActivity

        lifecycleScope.launch {
            try {
                userSession = User.getUserAsync()
            } catch (e: UserRetrievalException) {
                displayErrorMsg(getString(R.string.errorretrieveuser))
                return@launch
            } catch (e: Exception) {
                displayErrorMsg(getString(R.string.error_unknown) + " - " + e.toString())
                return@launch
            }

            binding = DataBindingUtil.setContentView(context, R.layout.welcome_user)
            showWelcomeScreen1()

            binding.welcomeHeader.text =
                context.getString(R.string.app_welcome, userSession.shortname ?: "")
            binding.welcomebutton1.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "welcomebutton1", "click")
                showWelcomeScreen2()
                binding.welcomebutton2.setOnClickListener {
                    AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "welcomebutton2", "click")
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            userSession.updateUserStatus("A", context)
                        } catch (e: Exception) {
                            println(e.message)
                            Log.e("UserUpdate", "Error updating status", e)
                        }
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@WelcomeActivity, ActivityContainer::class.java)
                            // Optional: clear previous tasks to prevent stacking
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun showWelcomeScreen1() {
        binding.welcomeScreen1.visibility = ConstraintLayout.VISIBLE
        binding.welcomeScreen2.visibility = ConstraintLayout.INVISIBLE
    }

    private fun showWelcomeScreen2() {
        binding.welcomeScreen1.visibility = ConstraintLayout.INVISIBLE
        binding.welcomeScreen2.visibility = ConstraintLayout.VISIBLE
    }

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        const val SCREEN_NAME = "WelcomeFragment"
        const val TAG = "WelcomeFragment"
    }
}
