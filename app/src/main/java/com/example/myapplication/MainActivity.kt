package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toolbar
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

var currentCity: Int = 0
const val PROP_CUR_DATE = "cur_date"
const val PROP_CITY = "current_city"
const val PROP_DATA = "current_data"

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: MainAdapter
    lateinit var adapterInDay: MainAdapter
    lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onItemClick: ((curDate: String) -> Unit)? = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fun (curDate: String) {
                if(data != null)
                    adapterInDay.dataSet = ArrayList(data).filter { it.date == curDate }
                val intent = Intent(this, Main2Activity::class.java)
                intent.putExtra(PROP_CUR_DATE, curDate)
                startActivity(intent)
            }
        } else {
            fun (curDate: String) {
                if(data != null)
                    adapterInDay.dataSet = data!!.filter { it.date == curDate }
                adapterInDay.notifyDataSetChanged()
            }
        }
        adapter = MainAdapter(this, ArrayList(), onItemClick)
        adapterInDay = MainAdapter(this, ArrayList())


        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        if(savedInstanceState == null)
            initParams()
        else
            renderData()


        citySelect?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setCity(position)
            }
        }
        btnRefreshCustom?.setOnClickListener { getDataRemote() }
    }


    private fun initParams() {
        sharedPreferences = getSharedPreferences("city", Context.MODE_PRIVATE)
        currentCity = sharedPreferences.getInt(PROP_CITY, 0)
        citySelect?.setSelection( getCityPositionById() )

        dataManager = DataManager(this)

        dataManager.loadData({this.renderData()})

    }


    private fun getDataLocal(): ArrayList<Meteo>? {
        val rawData = sharedPreferences.getString(PROP_DATA, null) ?: return null
        //Log.d("current", "getDataLocal " + rawData)
        val type: Type = (object: TypeToken<ArrayList<Meteo>>(){}).type
        return Gson().fromJson(rawData, type)
    }


    fun saveDataLocal(newData: ArrayList<Meteo>) {
        sharedPreferences.edit {
            //Log.d("current", "saveDataLocal " + Gson().toJson(newData, ArrayList::class.java))
            data = newData
            putString(PROP_DATA, Gson().toJson(newData, ArrayList::class.java))
            commit()
        }
    }


    fun getDataRemote() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MeteoApi.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val meteoApi = retrofit.create(MeteoApi::class.java)
        meteoApi.getRemote(currentCity)
            .enqueue(object: Callback<List<Meteo>>{
                override fun onFailure(call: Call<List<Meteo>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<Meteo>>, response: Response<List<Meteo>>) {
                    adapter.apply {
                        saveDataLocal(ArrayList(response.body()))
                        renderData()
                    }
                }
            })
    }


    fun getCityPositionById(id: Int = currentCity): Int {
        return resources.getIntArray(R.array.city_selector_id).lastIndexOf(id)
    }


    fun setCity(position: Int) {
        val newCity = resources.getIntArray(R.array.city_selector_id)[position]
        if(currentCity == newCity)
            return

        currentCity = newCity
        sharedPreferences.edit {
            putInt(PROP_CITY, newCity)
            commit()
        }
    }


    fun renderData() {
        if (data == null)
            return
        adapter.apply {
            dataSet = (ArrayList(data).filter { it.tod == "0" })
            notifyDataSetChanged()
        }
        adapterInDay.apply {
            dataSet = (ArrayList(data).filter { it.date == ArrayList(data).first().date })
            notifyDataSetChanged()
        }
    }
}

