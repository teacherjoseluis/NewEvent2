package com.bridesandgrooms.event.UI.Functions

import android.content.Context
import android.graphics.Paint

internal fun texttrimming(context: Context, text: String, maxwidthdp: Float): String {

    val paint = Paint()
    var textwidthpx = paint.measureText(text)//value in pixels
    val screenDensity = context.resources.displayMetrics.density // 160F
    var dptextsize = textwidthpx * screenDensity
    var croppedtext = text


    while (dptextsize >= maxwidthdp) {
        croppedtext = croppedtext.substring(0, (croppedtext.length) - 1)
        textwidthpx = paint.measureText(croppedtext)
        dptextsize = textwidthpx * screenDensity
    }

    if (croppedtext != text) {
        croppedtext.substring(0, (croppedtext.length) - 3)
        croppedtext += "..."
    }
    else {
        croppedtext = text
    }

    return croppedtext
}