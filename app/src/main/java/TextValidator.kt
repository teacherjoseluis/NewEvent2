//package com.example.newevent2.ui
//
//import android.text.Editable
//import android.text.TextWatcher
//import android.widget.TextView
//import com.example.newevent2.R
//
//
//class TextValidator(private val textView: TextView) : TextWatcher{
//    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//    }
//
//    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//    }
//
//    override fun afterTextChanged(p0: Editable?) {
//        when(textView.id){
//            R.id.taskname -> TextValidate(textView, textView.text.toString()).namefieldValidate()
//        }
//    }
//}