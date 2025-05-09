package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bridesandgrooms.event.OnboardingView
import com.bridesandgrooms.event.OnboardingView.Companion
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.databinding.EmptyStateFragmentBinding

/**
 * This is a generic fragment to use whenever there is no data to show. It accepts a Class that will be invoked by an Action Button
 */
class EmptyStateFragment : Fragment() {
    private lateinit var binding: EmptyStateFragmentBinding

    private var message: String? = null
    private var cta: String? = null
    private var actionClass: Class<*>? = null

    companion object {
        private const val SCREEN_NAME = "empty_state_fragment.xml"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString("message")
            cta = it.getString("cta")
            actionClass = it.getSerializable("actionClass") as Class<*>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.empty_state_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME,"EmptyState")
        binding.emptystateMessage.text = message
        binding.emptystateCta.text = cta
        binding.fabAction.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "fabAction", "click")
            try {
                val fragment = actionClass?.newInstance()
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        fragment as Fragment
                    )
                    .commit()
            } catch (e: Exception){
                Log.e("EmptyStateFragment.kt", e.message.toString())
            }
        }
    }
}
