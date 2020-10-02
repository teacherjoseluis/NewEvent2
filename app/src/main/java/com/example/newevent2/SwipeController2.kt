package com.example.newevent2

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build

import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SwipeController2(val context: Context, adapter: ItemTouchHelperAdapter2, recyclerView: RecyclerView) : ItemTouchHelper.Callback() {

    var swipeBack: Boolean = false

    //var mContext: Context
    //private var mClearPaint: Paint
    //private var mBackground: ColorDrawable
    //private var deleteDrawable: Drawable
    //var backgroundColor: Int = 0
    //var intrinsicHeight: Int = 0
    //var intrinsicWidth: Int = 0

    private lateinit var mAdapter:ItemTouchHelperAdapter2
    private lateinit var rv: RecyclerView

    val mContext = context
    val mBackground = ColorDrawable()
    val backgroundColor = Color.parseColor("#64b5f6")
    val mClearPaint = Paint()
    val checkDrawable = ContextCompat.getDrawable(mContext, R.drawable.icons8_shortcut_32)!!
    val checkintrinsicWidth = checkDrawable.intrinsicWidth
    val checkintrinsicHeight = checkDrawable.intrinsicHeight

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        checkDrawable.setTint(Color.WHITE)
        mAdapter=adapter
        rv=recyclerView
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
            RIGHT
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
        //val position = viewHolder.adapterPosition
        if (direction == RIGHT) {
            mAdapter.onItemSwiftRight2(viewHolder.adapterPosition, rv)
            //Toast.makeText(context, "Swipe to Delete", Toast.LENGTH_SHORT).show()
        }
    }

//    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
//        if (swipeBack) {
//            swipeBack = false
//            return 0
//        }
//        return super.convertToAbsoluteDirection(flags, layoutDirection)
//    }

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

        if (dX > 0) {// Swipe to Delete
            mBackground.color = backgroundColor
            mBackground.setBounds(
                itemView.left + dX.toInt(),
                itemView.top,
                itemView.left,
                itemView.bottom
            )
            mBackground.draw(c)

            val checkIconTop = itemView.top + (itemHeight - checkintrinsicHeight) / 2
            val checkIconMargin = (itemHeight - checkintrinsicHeight) / 2
            val checkIconLeft = itemView.left + checkIconMargin
            val checkIconRight = itemView.left + checkIconMargin + checkintrinsicWidth
            val checkIconBottom = checkIconTop + checkintrinsicHeight

            checkDrawable.setBounds(checkIconLeft, checkIconTop, checkIconRight, checkIconBottom)
            checkDrawable.draw(c)
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

interface ItemTouchHelperAdapter2 {
    fun onItemSwiftRight2(position: Int, recyclerView: RecyclerView)
}