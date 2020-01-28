package com.example.bmiapp.ui.input

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bmiapp.R
import kotlinx.android.synthetic.main.fragment_input.*
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
    private lateinit var keyList: MutableList<String>
    //同日の計測データの存在判定真理値
    private var isFirst = true
    //保存日の日付
    private var strToday = ""
    //入力値の正誤判定真理値
    private var isCorrectValue = false
    private lateinit var res: Resources
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        keyList = mutableListOf()
        //keyListにデータ詰める
        keyList.add(sharedPreferences.all.toString())
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
        //初回かどうか判定
        isFirst()
        editor = sharedPreferences.edit()
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
                editor.putString("bmi", bmi)
                editor.commit()
            }
        }
        //削除ボタン押下処理
        delete_button.setOnClickListener {
            //初回かどうか判定
            isFirst()
            //同日のデータが存在する場合、データを削除する
            if (isFirst) {
                //リストから削除
                keyList.removeAll { it == strToday }
                //sharedから削除
                editor.remove(strToday)
                editor.commit()
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
        if (isFirstDataOfMonth()) {
            editor.putString(
                strToday,
                strToday + "," + (height.text).toString() + "," + (weight.text).toString() + "," + bmi + "," + message + "," + "first"
            )
            //TODO:テストデータ
//            editor.putString(
//                "20200204",
//                "20200204" + "," + (height.text).toString() + "," + (weight.text).toString() + "," + bmi + "," + message + "," + "notFirst"
//            )
        } else {
            editor.putString(
                strToday,
                strToday + "," + (height.text).toString() + "," + (weight.text).toString() + "," + bmi + "," + message + "," + "notFirst"
            )
        }
        editor.commit()
        //同日のデータが存在しない場合
        if (isFirst) {
            //保存日の日付をkeyに保存する
            keyList.add(strToday)
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
    private fun onCreateDialog(errorMessage: String): Dialog {
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
    private fun isFirst() {
        var today = Date()
        var sdf = SimpleDateFormat("yyyyMMdd")
        //TODO:テストデータ
//        strToday = "20200303"
        strToday = sdf.format(today)
        for (date in keyList) {
            //同日のデータが存在する場合
            if (date == strToday) {
                isFirst = false
            }
        }
    }

    /**
     * 同月のデータが存在するかチェックするメソッド
     */
    private fun isFirstDataOfMonth(): Boolean {
        var isFirstDataOfMonth = false
        var today = Date()
        var sdf = SimpleDateFormat("yyyyMM")
        var sToday = sdf.format(today)
        Log.d("loglog", "today:" + sToday)
        val regex = Regex(sToday)
        Log.d("loglog", "regex:" + regex)
        //TODO:keyListをkeyのみにする？
        //TODO:もしくは、containを使う？
        for (date in keyList) {
            Log.d("loglog", "date:" + date)
            //同月のデータが存在しない場合
            if (!date.matches(regex)) {
                Log.d("loglog", "!date.matches(regex):" + true)
                isFirstDataOfMonth = true
            }
        }
        Log.d("loglog", "isFirstDataOfMonth:" + isFirstDataOfMonth)
        return isFirstDataOfMonth
    }

    /**
     * 入力された値が適切かどうかチェックするメソッド
     */
    private fun checkInputValue(): Boolean {
        var strHeight = (height.text).toString()
        var strWeight = (weight.text).toString()
        if (strHeight == null || strHeight.isEmpty()) {
            onCreateDialog(res.getString(R.string.error_01)).show()
            return false
        }
        if (strWeight == null || strWeight.isEmpty()) {
            onCreateDialog(res.getString(R.string.error_01)).show()
            return false
        }
        var floatHeight = (height.text).toString().toFloat()
        var floatWeight = (weight.text).toString().toFloat()
        var textForValidate = formatFirstDecimal(floatHeight)
        var textForValidate1 = formatInteger(floatHeight)
        var textForValidate2 = formatFirstDecimal(floatWeight)
        var textForValidate3 = formatInteger(floatWeight)
        if (strHeight != textForValidate
            && strHeight != textForValidate1
        ) {
            onCreateDialog(res.getString(R.string.error_02)).show()
            return false
        }
        if (strWeight != textForValidate2
            && strWeight != textForValidate3
        ) {
            onCreateDialog(res.getString(R.string.error_02)).show()
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
}