package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SwipeControllerTasks(
    val context: Context,
    adapter: ItemTouchAdapterAction,
    recyclerView: RecyclerView,
    private var leftaction: String?,
    private var rightaction: String?
) : ItemTouchHelper.Callback() {

    private val mClearPaint = Paint()
    private var mAdapter: ItemTouchAdapterAction
    private var rv: RecyclerView

    private var swipeBack: Boolean = false

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
                        recyclerView
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
                val itemView = viewHolder.itemView
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

                    val iconTop = itemView.top + (itemHeight - rightcontrol.intrinsicHeight!!) / 2
                    val iconMargin = (itemHeight - rightcontrol.intrinsicHeight!!) / 2
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + rightcontrol.intrinsicWidth!!
                    val iconBottom = iconTop + rightcontrol.intrinsicWidth!!

                    rightcontrol.drawable!!.setBounds(
                        iconLeft,
                        iconTop,
                        iconRight,
                        iconBottom
                    )
                    rightcontrol.drawable!!.draw(c)
                } else {// Swipe to Check
                    if (leftaction == "check") {
                        val leftcontrol = SwipeControlCheck(context)

                        leftcontrol.background.color = leftcontrol.backgroundcolor
                        leftcontrol.background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        leftcontrol.background.draw(c)

                        val iconTop = itemView.top + (itemHeight - leftcontrol.intrinsicHeight) / 2
                        val iconMargin = (itemHeight - leftcontrol.intrinsicHeight) / 2
                        val iconLeft = itemView.right - iconMargin - leftcontrol.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        val iconBottom = iconTop + leftcontrol.intrinsicHeight

                        leftcontrol.drawable.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        leftcontrol.drawable.draw(c)
                    }
                }
            }
        }
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        recyclerView: RecyclerView
    ) {
        recyclerView.setOnTouchListener { _, motionEvent ->
            swipeBack =
                motionEvent.action == MotionEvent.ACTION_CANCEL || motionEvent.action == MotionEvent.ACTION_UP
            false
        }
    }
}
