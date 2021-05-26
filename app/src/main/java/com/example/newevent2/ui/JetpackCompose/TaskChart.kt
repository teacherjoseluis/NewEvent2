//package com.example.newevent2.ui.JetpackCompose
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Color
//import android.graphics.Typeface
//import android.util.AttributeSet
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.AbstractComposeView
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.core.content.res.ResourcesCompat
//import com.example.newevent2.R
//import com.github.mikephil.charting.animation.Easing
//import com.github.mikephil.charting.charts.PieChart
//import com.github.mikephil.charting.components.Legend
//
//class TaskChart @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : AbstractComposeView(context, attrs, defStyleAttr) {
//
//    private lateinit var BandG_Colors: ArrayList<Int>
//    private lateinit var BandG_Colors2: ArrayList<Int>
//    private var tfLarge: Typeface? = null
//    private var tfRegular: Typeface? = null
//    private var tfLight: Typeface? = null
//    private lateinit var charttask: PieChart
//
//    val rosaPalido = context!!.resources.getColor(R.color.rosapalido)
//    val azulmasClaro = context!!.resources.getColor(R.color.azulmasClaro)
//    //val palodeRosa = context!!.resources.getColor(R.color.paloderosa)
//
//    @Composable
//    override fun Content() {
//
//        @SuppressLint("ResourceType")
//        BandG_Colors = arrayListOf(
//            rosaPalido,
//            azulmasClaro
//        )
//
////        BandG_Colors2 = arrayListOf(
////            azulmasClaro,
////            palodeRosa
////        )
//
//        tfLight = ResourcesCompat.getFont(context!!, R.font.raleway_thin)
//        tfRegular = ResourcesCompat.getFont(context!!, R.font.raleway_medium)
//        tfLarge = ResourcesCompat.getFont(context!!, R.font.raleway_medium)
//
//       // charttask = inf.findViewById(R.id.charttask)
//        charttask.apply {
//            setUsePercentValues(false)
//            description.isEnabled = false
//            setExtraOffsets(5f, 5f, 5f, 5f) //apparently this is padding
//            dragDecelerationFrictionCoef = 0.95f
//            setCenterTextTypeface(tfRegular)
//            setCenterTextColor(context!!.resources.getColor(R.color.rosaChillon))
//            setCenterTextSize(30f)
//            isDrawHoleEnabled = true
//            setHoleColor(Color.WHITE)
////            Don't really care too much about having a transparent circle
//            setTransparentCircleColor(Color.WHITE)
//            setTransparentCircleAlpha(110)
//            holeRadius = 50f
////            transparentCircleRadius = 61f
//            setDrawCenterText(true)
//            rotationAngle = 0f
//            isRotationEnabled = false
//            isHighlightPerTapEnabled = true
//            animateY(1400, Easing.EaseInOutQuad)
//            setEntryLabelColor(context!!.resources.getColor(R.color.Cream))
//            setEntryLabelTypeface(tfRegular)
//            setEntryLabelTextSize(14f)
//        }
//
//        charttask.legend.apply {
//            //formSize = 12.0f
//            form = Legend.LegendForm.CIRCLE
//            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
//            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//            textSize = 12.0f
//            textColor = (context!!.resources.getColor(R.color.secondaryText))
//            typeface = tfRegular
//            setDrawInside(false)
//            xEntrySpace = 10f
//            yEntrySpace = 4f
//            yOffset = 5f
//        }
//    }
//}