import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bridesandgrooms.event.R
import java.util.*


class TimePickerFragment(private val tv: TextView) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var calendar:Calendar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Initialize a Calendar instance
        calendar = Calendar.getInstance()

        // Get the system current hour and minute
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Create a TimePickerDialog with system current time
        // Return the TimePickerDialog
        return TimePickerDialog(
            activity, // Context
            //android.R.style.ThemeOverlay_Material_Dark, // Theme
            this, // TimePickerDialog.OnTimeSetListener
            hour, // Hour of day
            minute, // Minute
            false // Is 24 hour view
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the returned time
        //val tv:TextView = activity?.findViewById(R.id.etPlannedTime) as TextView
        val minutestring = String.format("%02d",minute)
        tv.text = "${getHourAMPM(hourOfDay)}:"+ minutestring + getAMPM(hourOfDay)
        //val timeValue = String.format("%02d",hour) + ":" + String.format("%02d",minute)
    }


    // When user cancel the time picker dialog
    override fun onCancel(dialog: DialogInterface) {
        Toast.makeText(activity,getString(R.string.pickercanceled),Toast.LENGTH_SHORT).show()
        super.onCancel(dialog)
    }


    // Custom method to get AM PM value from provided hour
    private fun getAMPM(hour:Int):String{
        return if(hour>11)"PM" else "AM"
    }


    // Custom method to get hour for AM PM time format
    private fun getHourAMPM(hour:Int):String{
        // Return the hour value for AM PM time format
        var modifiedHour = if (hour>11)hour-12 else hour
        if (modifiedHour == 0){modifiedHour = 12}
        return String.format("%02d",modifiedHour)
        //return modifiedHour
    }
}