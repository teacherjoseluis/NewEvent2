import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.R

class GalleryAdapter(private val context: Context, private val dataList: List<Pair<Bitmap, String>>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_gallery_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val (bitmap, photographer) = dataList[position]
        holder.bind(bitmap, photographer)
        holder.itemView.setOnClickListener {
            val uri = Uri.parse(photographer)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle the case where no activity can handle the intent
                Log.e("GalleryAdapter", "No activity found to handle the intent")
            }
        }
    }


    override fun getItemCount(): Int = dataList.size

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.categoryimage)
        private val categorycredit: TextView? = itemView.findViewById(R.id.photo_credits)
        //private val photographerTextView: TextView = itemView.findViewById(R.id.categorytitle)

        fun bind(bitmap: Bitmap, photographer: String) {
            imageView.setImageBitmap(bitmap)
            categorycredit?.text = photographer
            //photographerTextView.text = photographer
        }
    }
}
