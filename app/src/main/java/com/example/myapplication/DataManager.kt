package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

var data: ArrayList<Meteo>? = null


class DataManager(val ctx: Activity) {

    var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = ctx.getSharedPreferences("city", Context.MODE_PRIVATE)
    }

    fun loadData(renderData: (() -> Unit)? = null, force: Boolean = false) {
        if(force) {
            getDataRemote(renderData)
            return
        }
        data = getDataLocal()
        if(data != null)
            renderData?.invoke()
        else
            getDataRemote(renderData)
    }


    fun getDataLocal(renderData: (() -> Unit)? = null): ArrayList<Meteo>? {

        val db = Room.databaseBuilder(ctx, MeteoDatabase::class.java, "meteo").allowMainThreadQueries().build()
        data = ArrayList(db.meteoDao().getAll())

        /*
        val rawData = sharedPreferences.getString(PROP_DATA, null) ?: return null
        val type: Type = (object: TypeToken<ArrayList<Meteo>>(){}).type
        data = Gson().fromJson(rawData, type)
        */

        return data
    }

    fun getDataRemote(renderData: (() -> Unit)? = null) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MeteoApi.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val meteoApi = retrofit.create(MeteoApi::class.java)
        meteoApi.getRemote(currentCity)
            .enqueue(object: Callback<List<Meteo>> {
                override fun onFailure(call: Call<List<Meteo>>, t: Throwable) {
                    data = ArrayList<Meteo>()
                }

                override fun onResponse(call: Call<List<Meteo>>, response: Response<List<Meteo>>) {
                    data = ArrayList<Meteo>(response.body())
                    renderData?.invoke()

                    val db = Room.databaseBuilder(ctx, MeteoDatabase::class.java, "meteo").allowMainThreadQueries().build()
                    db.meteoDao().deleteAll()
                    for(m in ArrayList(data))
                        db.meteoDao().insertAll(m)
                }
            })
    }
}