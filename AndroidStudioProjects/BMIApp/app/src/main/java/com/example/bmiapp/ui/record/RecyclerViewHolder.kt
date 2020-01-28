package com.example.bmiapp.ui.record

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmiapp.R

class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titleMonthTextView: TextView = view.findViewById(R.id.title_month)
    val titleLineVIew: View = view.findViewById(R.id.title_under_line)
    val itemDateTextView: TextView = view.findViewById(R.id.text_date)
    val itemHeightTextView: TextView = view.findViewById(R.id.text_height)
    val itemWeightTextView: TextView = view.findViewById(R.id.text_weight)
    val itemBmiTextView: TextView = view.findViewById(R.id.text_bmi)
    val itemMessageTextView: TextView = view.findViewById(R.id.text_message)
}