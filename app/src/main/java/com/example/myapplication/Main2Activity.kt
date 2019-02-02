package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class Main2Activity : AppCompatActivity() {

    lateinit var adapterInDay: MainAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapterInDay = MainAdapter(this, data!!.filter { it.date == intent.getStringExtra(PROP_CUR_DATE) })  //  some data was loaded, because this activity created from click on item
        setContentView(R.layout.activity_main2)
    }


    fun renderData() {
        if (data == null)
            return

        adapterInDay.apply {
            dataSet = (ArrayList(data).filter { it.date == ArrayList(data).first().date })
            notifyDataSetChanged()
        }
    }
}
