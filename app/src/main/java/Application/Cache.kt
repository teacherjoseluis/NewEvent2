package Application

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.createemptyBitmap
import com.bridesandgrooms.event.Functions.getImgfromSD
import com.bridesandgrooms.event.Functions.saveBitmaptoSD
import com.bridesandgrooms.event.Functions.saveURLImgtoSD
import com.bridesandgrooms.event.MVP.*
import com.bridesandgrooms.event.Model.*
import com.google.firebase.storage.StorageReference
import kotlin.reflect.KClass

class Cache<T : Any> {

    private lateinit var taskPresenter: TaskPresenter
    private lateinit var paymentPresenter: PaymentPresenter
    private lateinit var guestPresenter: GuestPresenter
    private lateinit var vendorPresenter: VendorPresenter
    private lateinit var eventPresenter: EventPresenter
    private lateinit var imagePresenter: ImagePresenter
    private lateinit var taskhelper: TaskDBHelper
    private lateinit var paymenthelper: PaymentDBHelper
    private lateinit var guesthelper: GuestDBHelper
    private lateinit var vendorhelper: VendorDBHelper
    private lateinit var eventhelper: EventDBHelper

    private var contextCache: Context

    constructor(context: Context, presenter: TaskPresenter) {
        contextCache = context
        taskPresenter = presenter
        taskhelper = TaskDBHelper()
    }

    constructor(context: Context, presenter: PaymentPresenter) {
        contextCache = context
        paymentPresenter = presenter
        paymenthelper = PaymentDBHelper()
    }

    constructor(context: Context, presenter: GuestPresenter) {
        contextCache = context
        guestPresenter = presenter
        guesthelper = GuestDBHelper()
    }

    constructor(context: Context, presenter: VendorPresenter) {
        contextCache = context
        vendorPresenter = presenter
        vendorhelper = VendorDBHelper()
    }

    constructor(context: Context, presenter: EventPresenter) {
        contextCache = context
        eventPresenter = presenter
        eventhelper = EventDBHelper()
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
            list[0] is Vendor -> {
                for (vendor in list) {
                    vendorhelper.insert(vendor as Vendor)
                }
            }
        }
    }

    fun save(event: Event) {
        eventhelper.insert(event)
    }

    fun save(category: String, storageRef: StorageReference) {
        storageRef.downloadUrl.addOnSuccessListener { p0 ->
            saveURLImgtoSD(
                category,
                p0,
                contextCache
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun save(context: Context, category: String, bitmap: Bitmap) {
        saveBitmaptoSD(context, category, bitmap)
    }

    fun loadarraylist(kClass: KClass<T>) {
        when (kClass) {
            Task::class -> {
                val arrayList = taskhelper.getTasks()
                if (arrayList!!.size != 0) {
                    taskPresenter.onArrayListT(arrayList)
                } else {
                    taskPresenter.onEmptyListT()
                }
            }

            Payment::class -> {
                val arrayList = paymenthelper.getPayments()!!
                if (arrayList.size != 0) {
                    paymentPresenter.onArrayListP(arrayList)
                } else {
                    paymentPresenter.onEmptyListP()
                }
            }

            Guest::class -> {
                val arrayList = guesthelper.getAllGuests()!!
                if (arrayList.size != 0) {
                    guestPresenter.onArrayListG(arrayList)
                } else {
                    guestPresenter.onEmptyListG()
                }
            }

            Vendor::class -> {
                val arrayList = vendorhelper.getAllVendors()!!
                if (arrayList.size != 0) {
                    vendorPresenter.onArrayListV(arrayList)
                } else {
                    vendorPresenter.onEmptyListV()
                }
            }

            Event::class -> {
                val event = eventhelper.getEvent()!!
                if (event.key != "") {
                    eventPresenter.onEvent(event)
                } else {
                    eventPresenter.onEventError()
                }
            }
        }
    }

    @SuppressLint("NewApi")
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

    interface VendorArrayListCacheData {
        fun onArrayListV(arrayList: ArrayList<Vendor>)
        fun onEmptyListV()
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

    companion object
}