import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun sendEmail(context: Context) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.type = "message/rfc822"
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@bridesandgrooms.us"))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject of email")
    emailIntent.putExtra(Intent.EXTRA_TEXT, "body of email")

    try {
        val chooserIntent = Intent.createChooser(emailIntent, "Send mail...")
        context.startActivity(chooserIntent)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
    }
}