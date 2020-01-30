@file:Suppress("DEPRECATION")

package com.example.bmiapp.ui.input

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bmiapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_input.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

/**
 * 入力画面クラス
 */
class InputFragment : Fragment() {

    //入力された身長
    private var inputHeight: Float = 0.0F
    //入力された体重
    private var inputWeight: Float = 0.0F
    //bmi
    private var bmi: String = ""
    //sharedのkeyリスト
    private var keyList: MutableList<String>? = null
    //同日の計測データの存在判定真理値
    private var isFirst = true
    //保存日の日付
    private var strToday = ""
    //入力値の正誤判定真理値
    private var isCorrectValue = false
    private var res: Resources? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    companion object {
        const val DATE = "日付"
        const val HEIGHT = "身長"
        const val WEIGHT = "体重"
        const val BMI = "BMI"
        const val MESSAGE = "メッセージ"
        const val ISFIRST = "初回判定"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = getDefaultSharedPreferences(requireContext())
        keyList = mutableListOf()
        res = resources
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    /**
     * ・初回計測かどうか判定する処理
     * ・BMI計算ボタン押下処理
     * ・削除ボタン押下処理
     * ・保存ボタン押下処理
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //keyListにデータ詰める
        keyList?.add(sharedPreferences?.all.toString())
        //初回かどうか判定
        isFirst()
        if (isFirst) {
            //同日のデータを表示
            showRecord()
        }
        editor = sharedPreferences?.edit()
        //BMI計算ボタン押下処理
        input_calc_button.setOnClickListener {
            //入力された値のバリデーションチェック
            isCorrectValue = checkInputValue()
            if (isCorrectValue) {
                //入力された身長を保存する
                inputHeight = (height.text).toString().toFloat() / 100
                //入力された体重を保存する
                inputWeight = (weight.text).toString().toFloat()
                //BMIを計算する
                bmi = formatFirstDecimal(inputWeight / (inputHeight * inputHeight))
                //計算結果を表示する
                result_bmi.text = bmi
                editor?.putString("bmi", bmi)
                editor?.apply()
            }
        }
        //削除ボタン押下処理
        delete_button.setOnClickListener {
            //初回かどうか判定
            isFirst()
            //同日のデータが存在する場合、データを削除する
            if (isFirst) {
                //リストから削除
                keyList?.removeAll { it == strToday }
                //sharedから削除
                editor?.remove(strToday)
                editor?.commit()
            }
            //画面初期化処理
            initData()
        }
        //保存ボタン押下処理
        save_button.setOnClickListener {
            //入力された値のバリデーションチェック
            isCorrectValue = checkInputValue()
            if (isCorrectValue) {
                //保存するメソッドを呼び出す
                dataSave()
            }
        }
    }

    /**
     * データ保存メソッド
     */
    private fun dataSave() {
        //初回かどうか判定
        isFirst()
        //入力されたメッセージを保存する
        var message = ""
        if (result_message_free.text.toString() != null) {
            message = result_message_free.text.toString()
        }
        //データをmapに保存
        val map: MutableMap<String, String> = mutableMapOf()
        map[DATE] = strToday
        map[HEIGHT] = (height.text).toString()
        map[WEIGHT] = (weight.text).toString()
        map[BMI] = bmi
        map[MESSAGE] = message
        if (isFirstDataOfMonth()) {
            map[ISFIRST] = "first"

        } else {
            map[ISFIRST] = "notFirst"
        }
        val gson = Gson()
        val jsonString: String = gson.toJson(map)
        editor?.putString(strToday, jsonString)
        editor?.commit()
        //同日のデータが存在しない場合
        if (isFirst) {
            //保存日の日付をkeyに保存する
            keyList?.add(strToday)
        }
    }

    /**
     * 表示データ初期化メソッド
     */
    private fun initData() {
        height.setText("")
        weight.setText("")
        result_bmi.text = ""
        result_message_free.setText("")
    }

    /**
     * ダイアログ生成メソッド
     */
    private fun onCreateDialog(errorMessage: String?): Dialog {
        // ダイアログ生成  AlertDialogのBuilderクラスを指定してインスタンス化する
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        // 表示する文章設定
        dialogBuilder.setMessage(errorMessage)
        // 閉じるボタン作成
        dialogBuilder.setNegativeButton(
            "閉じる"
        ) { _, _ ->
            // 何もしないで閉じる
        }
        // dialogBuilderを返す
        return dialogBuilder.create()
    }

    /**
     * 同日のデータが存在するかチェックするメソッド
     */
    @SuppressLint("SimpleDateFormat")
    private fun isFirst() {
        val today = Date()
        val sdf = SimpleDateFormat("yyyyMMdd")
        strToday = sdf.format(today)
        for (date in this.keyList!!) {
            //同日のデータが存在する場合
            if (date == strToday) {
                isFirst = false
            }
        }
    }

    /**
     * 同月のデータが存在するかチェックするメソッド
     */
    @SuppressLint("SimpleDateFormat")
    private fun isFirstDataOfMonth(): Boolean {
        var isFirstDataOfMonth = false
        val today = Date()
        val sdf = SimpleDateFormat("yyyyMM")
        val sToday = sdf.format(today)
        val regex = Regex(sToday)
        for (date in this!!.keyList!!) {
            //同月のデータが存在しない場合
            if (!date.contains(regex)) {
                isFirstDataOfMonth = true
            }
        }
        return isFirstDataOfMonth
    }

    /**
     * 入力された値が適切かどうかチェックするメソッド
     */
    private fun checkInputValue(): Boolean {
        val strHeight = (height.text).toString()
        val strWeight = (weight.text).toString()
        if (strHeight == null || strHeight.isEmpty()) {
            onCreateDialog(res?.getString(R.string.error_01)).show()
            return false
        }
        if (strWeight == null || strWeight.isEmpty()) {
            onCreateDialog(res?.getString(R.string.error_01)).show()
            return false
        }
        val floatHeight = (height.text).toString().toFloat()
        val floatWeight = (weight.text).toString().toFloat()
        val textForValidate = formatFirstDecimal(floatHeight)
        val textForValidate1 = formatInteger(floatHeight)
        val textForValidate2 = formatFirstDecimal(floatWeight)
        val textForValidate3 = formatInteger(floatWeight)
        if (strHeight != textForValidate
            && strHeight != textForValidate1
        ) {
            onCreateDialog(res?.getString(R.string.error_02)).show()
            return false
        }
        if (strWeight != textForValidate2
            && strWeight != textForValidate3
        ) {
            onCreateDialog(res?.getString(R.string.error_02)).show()
            return false
        }
        return true
    }

    /**
     * 式数の値を小数点第一位にフォーマットするメソッド
     */
    private fun formatFirstDecimal(value: Float): String {
        return String.format(
            "%1$.1f", value
        )
    }

    /**
     * 式数の値を整数にフォーマットするメソッド
     */
    private fun formatInteger(value: Float): String {
        return String.format(
            "%1$.0f", value
        )
    }

    /**
     * 同日のデータを表示するメソッド
     */
    private fun showRecord() {
        val data = sharedPreferences!!.getString(strToday, "")
        val gson = Gson()
        val type: Type = object : TypeToken<MutableMap<String, String>>() {}.type
        try {
            val map: MutableMap<String, String> = gson.fromJson(data, type)
            height.setText(map[HEIGHT])
            weight.setText(map[WEIGHT])
            result_bmi.text = map[BMI]
            result_message_free.setText(map[MESSAGE])
        } catch (e: IllegalStateException) {
        }
    }
}