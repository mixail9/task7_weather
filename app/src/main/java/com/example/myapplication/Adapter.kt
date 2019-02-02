package com.example.myapplication

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.nodes.Entities


class MainAdapter(val ctx: Activity, var dataSet: List<Meteo>, val onClick: ((curDate: String)->Unit)? = null): RecyclerView.Adapter<MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(LayoutInflater.from(ctx).inflate(R.layout.item, parent, false) as ConstraintLayout)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        with(holder.view) {
            with(dataSet.get(position)) {
                findViewById<TextView>(R.id.vDate).text = date
                findViewById<TextView>(R.id.vPressure).text = pressure
                findViewById<TextView>(R.id.vTemp).text = Entities.unescape(temp)

                if(onClick != null) {
                    setOnClickListener { onClick.invoke(date) }
                }
            }
        }
    }
}


class MainHolder(val view: ConstraintLayout): RecyclerView.ViewHolder(view)