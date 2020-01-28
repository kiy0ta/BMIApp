package com.example.bmiapp.ui.record

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmiapp.R

/**
 * リサイクルビューを表示するためのアダプタークラス
 */
class RecycleViewAdapter(
    private val context: Context?,
    private val list: MutableList<String>
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    var res: Resources = context!!.resources
    private var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder?.let {
            //日付を整形する
            var item = list[position]
            var length = item.length
            var date = item.substring(length - 2, length)
            if (date.startsWith("0")) {
                date = item.substring(length - 1, length)
            }
            var month = item.substring(4, 6)
            if (month.startsWith("0")) {
                month = item.substring(5, 6)
            }
            //valueを取得する
            var data = sharedPreferences.getString(list[position], "")
            var itemList = mutableListOf<String>()
            if (data != null) {
                itemList = data.split(",").toMutableList()
            }
            //月タイトルの表示判定
            if (itemList[5] == "first") {
                it.titleMonthTextView.visibility = VISIBLE
                it.titleMonthTextView.text = month + res.getString(R.string.record_month)
                it.titleLineVIew.visibility = VISIBLE
            }
            it.itemDateTextView.text = date + res.getString(R.string.record_day)
            it.itemHeightTextView.text =
                res.getString(R.string.record_height) + itemList[1] + res.getString(R.string.cm)
            it.itemWeightTextView.text =
                res.getString(R.string.record_weight) + itemList[2] + res.getString(R.string.kg)
            it.itemBmiTextView.text = res.getString(R.string.record_bmi) + itemList[3]
            //TODO:messageの処理、デフォルトによっては変更を検討する
            if (itemList[4] != "") {
                it.itemMessageTextView.visibility = VISIBLE
                it.itemMessageTextView.text = itemList[4]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.item_record, parent, false)
        return RecyclerViewHolder(mView)
    }
}