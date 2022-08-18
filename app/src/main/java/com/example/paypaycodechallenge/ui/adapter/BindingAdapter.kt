package com.example.paypaycodechallenge.ui.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.math.RoundingMode
import java.text.DecimalFormat

@BindingAdapter("getFormatedValue")
fun getFormatedValue(textView: TextView, value: Double){

    val df = DecimalFormat("#.######")
    df.roundingMode = RoundingMode.CEILING

    var new_value = df.format(value).toDouble()
    var text:String  =  new_value.toString()
    textView.text = text

}