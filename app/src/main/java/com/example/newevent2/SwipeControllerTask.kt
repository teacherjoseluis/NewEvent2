package com.example.newevent2

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SwipeControllerTasks(
    val context: Context,
    adapter: ItemTouchAdapterAction,
    recyclerView: RecyclerView,
    leftaction: String?,
    rightaction: String?
) : ItemTouchHelper.Callback() {

    private val mClearPaint = Paint()
    private var mAdapter: ItemTouchAdapterAction
    private var rv: RecyclerView

    private var leftaction = leftaction
    private var rightaction = rightaction

    var swipeBack: Boolean = false

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mAdapter = adapter
        rv = recyclerView
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return when (leftaction != null) {
            true -> makeFlag(ACTION_STATE_SWIPE, LEFT or RIGHT)
            false -> makeFlag(ACTION_STATE_SWIPE, RIGHT)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == LEFT) {
            mAdapter.onItemSwiftLeft(viewHolder.adapterPosition, rv, leftaction!!)
        }
        if (direction == RIGHT) {
            mAdapter.onItemSwiftRight(viewHolder.adapterPosition, rv, rightaction!!)
        }
    }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        when (viewHolder) {
            !is Rv_TaskAdapter.NativeAdViewHolder -> {

                if (actionState == ACTION_STATE_SWIPE) {
                    setTouchListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                //---------------------------------------------------------------------------------------------
                val itemView = viewHolder!!.itemView
                val itemHeight = itemView.height

                val isCancelled = dX == 0.0f && !isCurrentlyActive

                if (isCancelled) {
                    clearCanvas(
                        c,
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    super.onChildDrawOver(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }

                if (dX > 0) {// Swipe to Delete

                    var rightcontrol: SwipeControl? = null
                    if (rightaction == "delete") {
                        rightcontrol = SwipeControlDelete(context)
                    } else if (rightaction == "undo") {
                        rightcontrol = SwipeControlUndo(context)
                    }

                    rightcontrol!!.background.color = rightcontrol.backgroundcolor!!
                    rightcontrol.background.setBounds(
                        itemView.left + dX.toInt(),
                        itemView.top,
                        itemView.left,
                        itemView.bottom
                    )
                    rightcontrol.background.draw(c)

                    val IconTop = itemView.top + (itemHeight - rightcontrol.intrinsicHeight!!) / 2
                    val IconMargin = (itemHeight - rightcontrol.intrinsicHeight!!) / 2
                    val IconLeft = itemView.left + IconMargin
                    val IconRight = itemView.left + IconMargin + rightcontrol.intrinsicWidth!!
                    val IconBottom = IconTop + rightcontrol.intrinsicWidth!!

                    rightcontrol.drawable!!.setBounds(
                        IconLeft,
                        IconTop,
                        IconRight,
                        IconBottom
                    )
                    rightcontrol.drawable!!.draw(c)
                } else {// Swipe to Check
                    if (leftaction == "check") {
                        var leftcontrol = SwipeControlCheck(context)

                        leftcontrol.background.color = leftcontrol.backgroundcolor
                        leftcontrol.background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        leftcontrol.background.draw(c)

                        val IconTop = itemView.top + (itemHeight - leftcontrol.intrinsicHeight) / 2
                        val IconMargin = (itemHeight - leftcontrol.intrinsicHeight) / 2
                        val IconLeft = itemView.right - IconMargin - leftcontrol.intrinsicWidth
                        val IconRight = itemView.right - IconMargin
                        val IconBottom = IconTop + leftcontrol.intrinsicHeight

                        leftcontrol.drawable.setBounds(IconLeft, IconTop, IconRight, IconBottom)
                        leftcontrol.drawable.draw(c)
                    }
                }
            }
        }
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { view, motionEvent ->
            swipeBack =
                motionEvent.action == MotionEvent.ACTION_CANCEL || motionEvent.action == MotionEvent.ACTION_UP
            false
        }
    }
}
