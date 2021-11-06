package Application

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.example.newevent2.Functions.calculateTimeDifferenceMs
import com.example.newevent2.Functions.getMockUserSetTime
import com.example.newevent2.NotificationID
import com.example.newevent2.NotificationJobService

class Notification {

    fun sendnotification(context:Context, title:String, body:String){
        val (userSetHourOfDay, userSetMinute) = getMockUserSetTime()
        val timeToWaitBeforeExecuteJob =
            calculateTimeDifferenceMs(userSetHourOfDay, userSetMinute)

        val jobID = NotificationID.getID()
        val bundle = PersistableBundle()
        bundle.putString("title", title)
        bundle.putString("body", body)

        (context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).run {
            schedule(
                JobInfo.Builder(
                    jobID,
                    ComponentName(context, NotificationJobService::class.java)
                )
                    // job execution will be delayed by this amount of time
                    .setMinimumLatency(timeToWaitBeforeExecuteJob)
                    // job will be run by this deadline
                    .setOverrideDeadline(timeToWaitBeforeExecuteJob)
                    .setExtras(bundle)
                    .build()
            )
        }
    }
}