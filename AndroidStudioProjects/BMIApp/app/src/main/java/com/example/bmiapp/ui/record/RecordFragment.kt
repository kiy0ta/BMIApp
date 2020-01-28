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
class RecordFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var sharedPreferences: SharedPreferences

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
        var recordRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
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
        var list = mutableListOf<String>()
        var map = sharedPreferences.all as Map<String, Any>
        var key = map.keys
        for (item in key) {
            if (item != "bmi") {
                list.add(item)
            }
        }
        list.sort()
        return list
    }
}