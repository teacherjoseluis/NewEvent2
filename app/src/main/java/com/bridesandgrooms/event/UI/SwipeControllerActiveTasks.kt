package com.bridesandgrooms.event.UI
//
//import android.content.Context
//import android.graphics.*
//import android.graphics.drawable.ColorDrawable
//import android.os.Build
//import android.view.MotionEvent
//import androidx.annotation.RequiresApi
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.ItemTouchHelper.*
//import androidx.recyclerview.widget.RecyclerView
//
//
//@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//class SwipeControllerActiveTasks(val context: Context, adapter: ItemTouchHelperAdapter, recyclerView: RecyclerView) : ItemTouchHelper.Callback() {
//
//    var swipeBack: Boolean = false
//
//    private var mAdapter:ItemTouchHelperAdapter
//    private var rv: RecyclerView
//
//    private val mContext = context
//    private val mBackgrounddelete = ColorDrawable()
//    private val mBackgroundcheck = ColorDrawable()
//    private val backgroundColorcheck = Color.parseColor("#c8e6c9")
//    private val backgroundColordelete = Color.parseColor("#D32F2F")
//    private val mClearPaint = Paint()
//    private val checkDrawable = ContextCompat.getDrawable(mContext, R.drawable.icons8_checkmark)!!
//    private val deleteDrawable= ContextCompat.getDrawable(mContext, R.drawable.icons8_trash_can)!!
//    private val checkintrinsicWidth = checkDrawable.intrinsicWidth
//    private val checkintrinsicHeight = checkDrawable.intrinsicHeight
//    private val deleteintrinsicWidth = deleteDrawable.intrinsicWidth
//    private val deleteintrinsicHeight = deleteDrawable.intrinsicHeight
//
//    init {
//        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
//        checkDrawable.setTint(Color.WHITE)
//        deleteDrawable.setTint(Color.WHITE)
//        mAdapter=adapter
//        rv=recyclerView
//    }
//
//    override fun getMovementFlags(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder
//    ): Int {
//        return makeFlag(
//            ACTION_STATE_IDLE,
//            RIGHT
//        ) or makeFlag(
//            ACTION_STATE_SWIPE,
//            LEFT or RIGHT
//        )
//    }
//
//    override fun onMove(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ): Boolean {
//        return false
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        if (direction == LEFT) {
//            mAdapter.onItemSwiftLeft(viewHolder.adapterPosition, rv)
//        }
//        if (direction == RIGHT) {
//            mAdapter.onItemSwiftRight(viewHolder.adapterPosition, rv)
//        }
//    }
//
//    override fun onChildDraw(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//        if (actionState == ACTION_STATE_SWIPE) {
//            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//        }
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//
//        //---------------------------------------------------------------------------------------------
//        val itemView = viewHolder!!.itemView
//        val itemHeight = itemView.height
//
//        val isCancelled = dX == 0.0f && !isCurrentlyActive
//
//        if (isCancelled) {
//            clearCanvas(
//                c,
//                itemView.right + dX,
//                itemView.top.toFloat(),
//                itemView.right.toFloat(),
//                itemView.bottom.toFloat()
//            )
//            super.onChildDrawOver(
//                c,
//                recyclerView,
//                viewHolder,
//                dX,
//                dY,
//                actionState,
//                isCurrentlyActive
//            )
//            return
//        }
//
//        if (dX > 0) {// Swipe to Delete
//            mBackgrounddelete.color = backgroundColordelete
//            mBackgrounddelete.setBounds(
//                itemView.left + dX.toInt(),
//                itemView.top,
//                itemView.left,
//                itemView.bottom
//            )
//            mBackgrounddelete.draw(c)
//
//            val deleteIconTop = itemView.top + (itemHeight - deleteintrinsicHeight) / 2
//            val deleteIconMargin = (itemHeight - deleteintrinsicHeight) / 2
//            val deleteIconLeft = itemView.left + deleteIconMargin
//            val deleteIconRight = itemView.left + deleteIconMargin + deleteintrinsicWidth
//            val deleteIconBottom = deleteIconTop + deleteintrinsicHeight
//
//            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
//            deleteDrawable.draw(c)
//        } else {// Swipe to Check
//            mBackgroundcheck.color = backgroundColorcheck
//            mBackgroundcheck.setBounds(
//                itemView.right + dX.toInt(),
//                itemView.top,
//                itemView.right,
//                itemView.bottom
//            )
//            mBackgroundcheck.draw(c)
//
//            val checkIconTop = itemView.top + (itemHeight - checkintrinsicHeight) / 2
//            val checkIconMargin = (itemHeight - checkintrinsicHeight) / 2
//            val checkIconLeft = itemView.right - checkIconMargin - checkintrinsicWidth
//            val checkIconRight = itemView.right - checkIconMargin
//            val checkIconBottom = checkIconTop + checkintrinsicHeight
//
//            checkDrawable.setBounds(checkIconLeft, checkIconTop, checkIconRight, checkIconBottom)
//            checkDrawable.draw(c)
//        }
//    }
//
//    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
//        c.drawRect(left, top, right, bottom, mClearPaint)
//    }
//
//    private fun setTouchListener(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float, dY: Float,
//        actionState: Int, isCurrentlyActive: Boolean
//    ) {
//        recyclerView.setOnTouchListener { view, motionEvent ->
//            swipeBack =
//                motionEvent.action == MotionEvent.ACTION_CANCEL || motionEvent.action == MotionEvent.ACTION_UP
//            false
//        }
//    }
//}
