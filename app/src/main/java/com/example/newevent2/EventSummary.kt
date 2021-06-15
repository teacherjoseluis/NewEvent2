package com.example.newevent2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.baoyachi.stepview.HorizontalStepView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.newevent2.Functions.*
import com.example.newevent2.MVP.EventPresenter
import com.example.newevent2.MVP.GuestPresenter
import com.example.newevent2.MVP.ImagePresenter
import com.example.newevent2.Model.Event
import com.google.android.gms.maps.MapView
import kotlinx.android.synthetic.main.summary_weddingguests.view.*


class EventSummary : Fragment(), EventPresenter.ViewEventActivity, GuestPresenter.GuestStats,
    ImagePresenter.EventImage {

    var userid = ""
    var eventid = ""

    private lateinit var presenterevent: EventPresenter
    private lateinit var presenterguest: GuestPresenter
    private lateinit var mapview: MapView
    private lateinit var imagePresenter: ImagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        userid = this.arguments!!.get("userid").toString()
        eventid = this.arguments!!.get("eventid").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf = inflater.inflate(R.layout.event_summary, container, false)

        val editeventbutton = inf.findViewById<ImageView>(R.id.editEventButton)
        editeventbutton.setOnClickListener {
            val editevent = Intent(context, MainActivity::class.java)
            editevent.putExtra("userid", userid)
            editevent.putExtra("eventid", eventid)
            startActivityForResult(editevent, SUCCESS_RETURN)
            //startActivity(editevent)
        }

        // Get Event details -----------------------------------------------------------------------
        presenterevent = EventPresenter(this.context!!,this, inf, userid, eventid)
        presenterevent.getEventDetail()
        //------------------------------------------------------------------------------------------
        presenterguest = GuestPresenter(this.context!!,this, inf)
        presenterguest.getGuestsEvent()
        presenterguest.userid = userid
        presenterguest.eventid = eventid

        //------------------------------------------------------------------------------------------
//        mapview = inf.findViewById<MapView>(R.id.mapView)
//        mapview.onCreate(savedInstanceState)
//        mapview.onResume()
        //------------------------------------------------------------------------------------------
        val stepview = inf.findViewById<HorizontalStepView>(R.id.step_view)
        val user = com.example.newevent2.Functions.getUserSession(context!!)
        val stepsBeanList = user.onboardingprogress()

        stepview
            .setStepViewTexts(stepsBeanList)//总步骤
            .setTextSize(12)//set textSize
            .setStepsViewIndicatorCompletedLineColor(context!!.resources.getColor(R.color.azulmasClaro))//设置StepsViewIndicator完成线的颜色
            .setStepsViewIndicatorUnCompletedLineColor(context!!.resources.getColor(R.color.rosaChillon))//设置StepsViewIndicator未完成线的颜色
            .setStepViewComplectedTextColor(context!!.resources.getColor(R.color.azulmasClaro))//设置StepsView text完成线的颜色
            .setStepViewUnComplectedTextColor(context!!.resources.getColor(R.color.rosaChillon))//设置StepsView text未完成线的颜色
            .setStepsViewIndicatorCompleteIcon(context!!.resources.getDrawable(R.drawable.icons8_checked_rosachillon))//设置StepsViewIndicator CompleteIcon
            .setStepsViewIndicatorDefaultIcon(context!!.resources.getDrawable(R.drawable.circle_rosachillon))//设置StepsViewIndicator DefaultIcon
            .setStepsViewIndicatorAttentionIcon(context!!.resources.getDrawable(R.drawable.alert_icon_rosachillon));//设置StepsViewIndicator AttentionIcon
        return inf
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewEventSuccessFragment(context: Context, inflatedView: View, event: Event) {
        inflatedView.findViewById<TextView>(R.id.eventname).text = event.name
        inflatedView.findViewById<TextView>(R.id.eventdate).text = event.date
        //inflatedView.findViewById<TextView>(R.id.textView4).text = event.time
        //inflatedView.findViewById<TextView>(R.id.eventabout).text = event.about
        //inflatedView.findViewById<TextView>(R.id.MyLocation).text = event.location
        inflatedView.findViewById<TextView>(R.id.eventaddress).text = event.location

        val wedavater = inflatedView.findViewById<ImageView>(R.id.weddingavatar)

        val daysleft = daystoDate(converttoDate(event.date))
        inflatedView.findViewById<TextView>(R.id.deadline).text = "$daysleft days left!"

        // Load thumbnail
        imagePresenter = ImagePresenter(context, this, inflatedView)
        imagePresenter.userid = userid
        imagePresenter.eventid = eventid
        imagePresenter.getEventImage()

//        val eventLocationLnLg = LatLng(event.latitude, event.longitude)
//        val latLng = LatLngBounds(
//            LatLng(event.latitude - 5, event.longitude - 5),
//            LatLng(event.latitude + 5, event.longitude + 5)
//        )

//        mapview.getMapAsync { p0 ->
//            val googleMap = p0!!
//            googleMap!!.addMarker(
//                MarkerOptions().position(eventLocationLnLg).title(event.location)
//            )
//            googleMap!!.moveCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    latLng!!.center,
//                    16f
//                )
//            )
//            googleMap!!.uiSettings.setAllGesturesEnabled(false)
//
//            mapView.setOnClickListener(object : View.OnClickListener {
//                val uri =
//                    String.format(Locale.ENGLISH, "geo:%f,%f", event.latitude, event.longitude)
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//                override fun onClick(p0: View?) {
//                    context!!.startActivity(intent)
//                }
//            })
//        }

//        inflatedView.editEventButton.setOnClickListener {
//            val eventedit = Intent(context, Event_EditDetail::class.java)
//            eventedit.putExtra("evententity", event)
//            context!!.startActivity(eventedit)
//        }
    }

    override fun onViewEventErrorFragment(inflatedView: View, errcode: String) {
        Toast.makeText(
            context,
            "There was an error trying to get Event data",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onGuestConfirmation(
        inflatedView: View,
        confirmed: Int,
        rejected: Int,
        pending: Int
    ) {
        //inflatedView.totalnumber.text = (confirmed + rejected + pending).toString()
        inflatedView.acceptednumber.text = confirmed.toString()
        inflatedView.rejectednumber.text = rejected.toString()
        inflatedView.pendingnumber.text = pending.toString()
    }

    companion object {
        const val WRITE_EXTERNAL_STORAGE = 0
        const val REQUEST_PERMISSION = 0
        const val SUCCESS_RETURN = 1
    }

    override fun onEventImage(mContext: Context, inflatedView: View?, packet: Any) {
        val wedavater = inflatedView!!.findViewById<ImageView>(R.id.weddingavatar)

        Glide.with(mContext)
            .load(packet)
            .apply(RequestOptions.circleCropTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).placeholder(R.drawable.avatar2)
            .into(wedavater)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fm = activity!!.supportFragmentManager
        if (resultCode == Activity.RESULT_OK && requestCode == SUCCESS_RETURN) {
            Toast.makeText(activity, "Event changes saved successfully", LENGTH_LONG).show()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(Runnable {
                val user = com.example.newevent2.Functions.getUserSession(context!!)
                val newfragment =  MainEventView_clone(user)
                fm!!.beginTransaction()
                    .replace(R.id.fragment_container, newfragment)
                    // .addToBackStack(null)
                    .commit()
            }, 2000) //1 seconds
        }
    }
}


