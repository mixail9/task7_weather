package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.list.*

class ListFragment: Fragment() {

    private var listRefresh: SwipeRefreshLayout? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.list, container, false)

        val list = fragment.findViewById<RecyclerView>(R.id.listInFragment)
        listRefresh = fragment.findViewById<SwipeRefreshLayout>(R.id.listRefresh)

        listRefresh?.setOnRefreshListener {
            Log.d("current", "refresh")
            DataManager(activity as Activity).loadData( { renderData() }, true)
            //listRefresh.isRefreshing = false

        }

        when {
            (id == R.id.frList1) -> {   //  first activity
                list.apply {
                    adapter = (activity as MainActivity).adapter
                    layoutManager = LinearLayoutManager(activity)
                    setBackgroundColor(resources.getColor(R.color.colorPrimary))
                }
            }
            (id == R.id.frList3) -> {   //  second activity
                list.apply {
                    adapter = (activity as Main2Activity).adapterInDay
                    layoutManager = LinearLayoutManager(activity)
                    setBackgroundColor(resources.getColor(R.color.colorAccent))


                }
            }
            else -> {  //  first activity inDay list
                list.apply {
                    adapter = (activity as MainActivity).adapterInDay
                    layoutManager = LinearLayoutManager(activity)
                    setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
            }
        }

        return fragment
    }


    fun renderData() {
        Log.d("current", "renderData " + data)
        if (data == null)
            return

        //view?.findViewById<SwipeRefreshLayout>(R.id.listRefresh).isRefreshing = false
        activity?.findViewById<SwipeRefreshLayout>(R.id.listRefresh)?.isRefreshing = false
        listRefresh?.isRefreshing = false

        if(activity is MainActivity)
            (activity as MainActivity).renderData()

        if(activity is Main2Activity)
            (activity as MainActivity).renderData()
    }
}