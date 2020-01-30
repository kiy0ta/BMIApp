@file:Suppress("DEPRECATION")

package com.example.bmiapp.ui.record

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmiapp.R

/**
 * 履歴画面クラス
 */
@Suppress("DEPRECATION")
class RecordFragment : Fragment() {

    private var viewAdapter: RecyclerView.Adapter<*>? = null
    private var viewManager: RecyclerView.LayoutManager? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //リサイクルビューを描画
        viewAdapter = RecycleViewAdapter(context, getList())
        viewManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val recordRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recordRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    /**
     * sharedのkey名のリストを作成するメソッド
     */
    private fun getList(): MutableList<String> {
        val list = mutableListOf<String>()
        val map = sharedPreferences?.all as Map<String, Any>
        val key = map.keys
        for (item in key) {
            if (item != "bmi") {
                list.add(item)
            }
        }
        list.sort()
        return list
    }
}