package com.example.newevent2.ui.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment() {

        private var listener: TimePickerDialog.OnTimeSetListener? = null

        fun onCreateDialog(savedInstanceState: () -> Int): Dialog {
            val c = Calendar.getInstance()
            val hour=c.get(Calendar.HOUR)
            val minute= c.get(Calendar.MINUTE)
            /*
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, minute)
            */

            return TimePickerDialog(this.activity!!,listener, hour, minute, true)

        }

        companion object {
            fun newInstance(
                listener: TimePickerDialog.OnTimeSetListener
            ): TimePickerFragment {
                val fragment = TimePickerFragment()
                fragment.listener = listener
                return fragment
            }
        }

    }

