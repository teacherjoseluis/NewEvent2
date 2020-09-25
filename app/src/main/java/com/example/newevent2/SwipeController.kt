package com.example.newevent2

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build

import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SwipeController(val context: Context) : ItemTouchHelper.Callback() {

    var swipeBack: Boolean = false

    //var mContext: Context
    //private var mClearPaint: Paint
    //private var mBackground: ColorDrawable
    //private var deleteDrawable: Drawable
    //var backgroundColor: Int = 0
    //var intrinsicHeight: Int = 0
    //var intrinsicWidth: Int = 0

    val mContext = context
    val mBackground = ColorDrawable()
    val mBackground2 = ColorDrawable()
    val backgroundColor = Color.parseColor("#c8e6c9")
    val backgroundColor2 = Color.parseColor("#D32F2F")
    val mClearPaint = Paint()
    val checkDrawable = ContextCompat.getDrawable(mContext, R.drawable.icons8_checkmark)!!
    val deleteDrawable= ContextCompat.getDrawable(mContext, R.drawable.icons8_trash_can)!!
    val checkintrinsicWidth = checkDrawable.intrinsicWidth
    val checkintrinsicHeight = checkDrawable.intrinsicHeight
    val deleteintrinsicWidth = deleteDrawable.intrinsicWidth
    val deleteintrinsicHeight = deleteDrawable.intrinsicHeight

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        checkDrawable.setTint(Color.WHITE)
        deleteDrawable.setTint(Color.WHITE)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        //return makeMovementFlags(0, LEFT or RIGHT)
        return makeFlag(
            ACTION_STATE_IDLE,
            RIGHT
        ) or makeFlag(
            ACTION_STATE_SWIPE,
            LEFT or RIGHT
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
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
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

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

        mBackground.color = backgroundColor
        mBackground.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        mBackground.draw(c)

        mBackground2.color = backgroundColor2
        mBackground2.setBounds(
            itemView.left + dX.toInt(),
            itemView.top,
            itemView.left,
            itemView.bottom
        )
        mBackground2.draw(c)


        val checkIconTop = itemView.top + (itemHeight - checkintrinsicHeight) / 2
        val checkIconMargin = (itemHeight - checkintrinsicHeight) / 2
        val checkIconLeft = itemView.right - checkIconMargin - checkintrinsicWidth
        val checkIconRight = itemView.right - checkIconMargin
        val checkIconBottom = checkIconTop + checkintrinsicHeight

        val deleteIconTop = itemView.top + (itemHeight - deleteintrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - deleteintrinsicHeight) / 2
        val deleteIconLeft = itemView.left + deleteIconMargin
        val deleteIconRight = itemView.left + deleteIconMargin + deleteintrinsicWidth
        val deleteIconBottom = deleteIconTop + deleteintrinsicHeight

        checkDrawable.setBounds(checkIconLeft, checkIconTop, checkIconRight, checkIconBottom)
        checkDrawable.draw(c)

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)
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

