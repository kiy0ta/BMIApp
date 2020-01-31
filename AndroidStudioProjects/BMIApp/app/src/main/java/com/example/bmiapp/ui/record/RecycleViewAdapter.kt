package com.example.bmiapp.ui.record

import android.annotation.SuppressLint
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
import com.example.bmiapp.ui.input.InputFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_input.*
import java.lang.reflect.Type

/**
 * リサイクルビューを表示するためのアダプタークラス
 */
@Suppress("DEPRECATION")
class RecycleViewAdapter(
    private val context: Context?,
    private val list: MutableList<String>
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    var res: Resources = context!!.resources
    private var sharedPreferences =
        context?.getSharedPreferences(context!!.packageName, Context.MODE_PRIVATE)

    companion object {
        const val DATE = "日付"
        const val HEIGHT = "身長"
        const val WEIGHT = "体重"
        const val BMI = "BMI"
        const val MESSAGE = "メッセージ"
        const val ISFIRST = "初回判定"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.let {
            //日付を整形する
            val item = list[position]
            val length = item.length
            var date = item.substring(length - 2, length)
            if (date.startsWith("0")) {
                date = item.substring(length - 1, length)
            }
            var month = item.substring(4, 6)
            if (month.startsWith("0")) {
                month = item.substring(5, 6)
            }
            //valueを取得する
            val data = sharedPreferences!!.getString(item, "")
            val gson = Gson()
            val type: Type = object : TypeToken<MutableMap<String, String>>() {}.type
            val map: MutableMap<String, String> = gson.fromJson(data, type)
            //月タイトルの表示判定
            if (map[ISFIRST] == "first") {
                it.titleMonthTextView.visibility = VISIBLE
                it.titleMonthTextView.text = month + res.getString(R.string.record_month)
                it.titleLineVIew.visibility = VISIBLE
            }
            it.itemDateTextView.text = date + res.getString(R.string.record_day)
            it.itemHeightTextView.text =
                res.getString(R.string.record_height) + map[HEIGHT] + res.getString(R.string.cm)
            it.itemWeightTextView.text =
                res.getString(R.string.record_weight) + map[WEIGHT] + res.getString(R.string.kg)
            it.itemBmiTextView.text = res.getString(R.string.record_bmi) + map[BMI]
            if (map[MESSAGE] != "") {
                it.itemMessageTextView.visibility = VISIBLE
                it.itemMessageTextView.text = map[MESSAGE]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.item_record, parent, false)
        return RecyclerViewHolder(mView)
    }
}