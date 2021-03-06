package Application

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.Functions.*
import com.example.newevent2.MVP.*
import com.example.newevent2.Model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.StorageReference
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

class Cache<T : Any> {

    private lateinit var taskPresenter: TaskPresenter
    private lateinit var paymentPresenter: PaymentPresenter
    private lateinit var guestPresenter: GuestPresenter
    private lateinit var eventPresenter: EventPresenter
    private lateinit var imagePresenter: ImagePresenter
    private lateinit var taskhelper: TaskDBHelper
    private lateinit var paymenthelper: PaymentDBHelper
    private lateinit var guesthelper: GuestDBHelper
    private lateinit var eventhelper: EventDBHelper

    private var contextCache: Context

    constructor(context: Context, presenter: TaskPresenter) {
        contextCache = context
        taskPresenter = presenter
        taskhelper = TaskDBHelper(context)
    }

    constructor(context: Context, presenter: PaymentPresenter) {
        contextCache = context
        paymentPresenter = presenter
        paymenthelper = PaymentDBHelper(context)
    }

    constructor(context: Context, presenter: GuestPresenter) {
        contextCache = context
        guestPresenter = presenter
        guesthelper = GuestDBHelper(context)
    }

    constructor(context: Context, presenter: EventPresenter) {
        contextCache = context
        eventPresenter = presenter
        eventhelper = EventDBHelper(context)
    }

    constructor(context: Context, presenter: ImagePresenter) {
        contextCache = context
        imagePresenter = presenter
    }

    fun save(list: ArrayList<*>) {
        when {
            list[0] is Task -> {
                for (task in list) {
                    taskhelper.insert(task as Task)
                }
            }
            list[0] is Payment -> {
                for (payment in list) {
                    paymenthelper.insert(payment as Payment)
                }
            }
            list[0] is Guest -> {
                for (guest in list) {
                    guesthelper.insert(guest as Guest)
                }
            }
        }
    }

    fun save(event: Event) {
        eventhelper.insert(event)
    }

    fun save(category: String, storageRef: StorageReference) {
        storageRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
            override fun onSuccess(p0: Uri?) {
                saveURLImgtoSD(category, p0, contextCache)
            }
        })
    }

    fun save(context: Context, category: String, bitmap: Bitmap){
        saveBitmaptoSD(context, category, bitmap)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadarraylist(kClass: KClass<T>) {
        when (kClass) {
            Task::class -> {
                val arrayList = taskhelper.getTasks()
                if (arrayList.size != 0) {
                    taskPresenter.onArrayListT(arrayList)
                } else {
                    taskPresenter.onEmptyListT()
                }
            }

            Payment::class -> {
                val arrayList = paymenthelper.getPayments()
                if (arrayList.size != 0) {
                    paymentPresenter.onArrayListP(arrayList)
                } else {
                    paymentPresenter.onEmptyListP()
                }
            }

            Guest::class -> {
                val arrayList = guesthelper.getAllGuests()
                if (arrayList.size != 0) {
                    guestPresenter.onArrayListG(arrayList)
                } else {
                    guestPresenter.onEmptyListG()
                }
            }

            Event::class -> {
                val event = eventhelper.getEvent()
                if (event.key != "") {
                    eventPresenter.onEvent(event)
                } else {
                    eventPresenter.onEventError()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadimage(cacheCategory: String) {
        val imagebitmap = getImgfromSD(cacheCategory, contextCache)
        val emptyBitmap = createemptyBitmap(250, 250)
        if (imagebitmap.sameAs(emptyBitmap)) {
            when (cacheCategory) {
                ImagePresenter.EVENTIMAGE -> imagePresenter.onEmptyEventImage("")
                ImagePresenter.PLACEIMAGE -> imagePresenter.onEmptyPlaceImage("")
            }
        } else {
            when (cacheCategory) {
                ImagePresenter.EVENTIMAGE -> imagePresenter.onEventImage(imagebitmap)
                ImagePresenter.PLACEIMAGE -> imagePresenter.onPlaceImage(imagebitmap)
            }
        }
    }

    interface TaskArrayListCacheData {
        fun onArrayListT(arrayList: ArrayList<Task>)
        fun onEmptyListT()
    }

    interface PaymentArrayListCacheData {
        fun onArrayListP(arrayList: ArrayList<Payment>)
        fun onEmptyListP()
    }

    interface GuestArrayListCacheData {
        fun onArrayListG(arrayList: ArrayList<Guest>)
        fun onEmptyListG()
    }

    interface EventItemCacheData {
        fun onEvent(item: Event)
        fun onEventError()
    }

    interface EventImageCacheData {
        fun onEventImage(image: Bitmap)
        fun onEmptyEventImage(errorcode: String)
    }

    interface PlaceImageCacheData {
        fun onPlaceImage(image: Bitmap)
        fun onEmptyPlaceImage(errorcode: String)
    }

    companion object {
        const val ONE_MINUTE_IN_MILLIS: Long = 60000
        private const val TAG = "Cache"

    }
}