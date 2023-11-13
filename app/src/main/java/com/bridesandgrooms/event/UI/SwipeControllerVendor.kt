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
//import androidx.recyclerview.widget.RecyclerView
//
//@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//class SwipeControllerVendor(val context: Context, adapter: Rv_VendorAdapter, recyclerView: RecyclerView) : ItemTouchHelper.Callback() {
//
//    var swipeBack: Boolean = false
//
//    private var mAdapter:ItemTouchHelperAdapter
//    private var rv: RecyclerView
//
//    private val mContext = context
//    private val mBackground = ColorDrawable()
//    private val backgroundColor = Color.parseColor("#D32F2F")
//    private val mClearPaint = Paint()
//    private val deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.icons8_trash_can)!!
//    private val deleteintrinsicWidth = deleteDrawable.intrinsicWidth
//    private val deleteintrinsicHeight = deleteDrawable.intrinsicHeight
//
//    init {
//        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
//        deleteDrawable.setTint(Color.WHITE)
//        mAdapter=adapter
//        rv=recyclerView
//    }
//
//    override fun getMovementFlags(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder
//    ): Int {
//        //return makeMovementFlags(0, LEFT or RIGHT)
//        return makeFlag(
//            ItemTouchHelper.ACTION_STATE_IDLE,
//            ItemTouchHelper.RIGHT
//        ) or makeFlag(
//            ItemTouchHelper.ACTION_STATE_SWIPE,
//            ItemTouchHelper.RIGHT
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
//        if (direction == ItemTouchHelper.RIGHT) {
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
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
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
//            mBackground.color = backgroundColor
//            mBackground.setBounds(
//                itemView.left + dX.toInt(),
//                itemView.top,
//                itemView.left,
//                itemView.bottom
//            )
//            mBackground.draw(c)
//
//            val deleteIconTop = itemView.top + (itemHeight - deleteintrinsicHeight) / 2
//            val deleteIconMargin = (itemHeight - deleteintrinsicHeight) / 2
//            val deleteIconLeft = itemView.left + deleteIconMargin
//            val deleteIconRight = itemView.left + deleteIconMargin + deleteintrinsicWidth
//            val deleteIconBottom = deleteIconTop + deleteintrinsicHeight
//
//            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
//            deleteDrawable.draw(c)
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
//
