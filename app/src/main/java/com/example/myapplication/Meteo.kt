package com.example.myapplication

import androidx.room.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface MeteoApi {
    companion object {
        val baseUrl = "http://icomms.ru/"
    }

    @GET("inf/meteo.php")
    fun getRemote(@Query("tid") cityId: Int): Call<List<Meteo>>
}


@Dao
interface MeteoApiLocal {

    @androidx.room.Query("SELECT * FROM meteo")
    fun getAll(): List<Meteo>

    @Insert
    fun insertAll(vararg meteos: Meteo)

    @androidx.room.Query("DELETE FROM meteo")
    fun deleteAll()
}

@Database(entities = [Meteo::class], version = 1)
abstract class MeteoDatabase: RoomDatabase() {
    abstract fun meteoDao(): MeteoApiLocal
}


@Entity(tableName = "meteo")
data class Meteo(@PrimaryKey(autoGenerate = true) var uid: Int = 0) {
    @ColumnInfo(name = "date") lateinit var date: String
    @ColumnInfo(name = "tod") lateinit var tod: String
    @ColumnInfo(name = "pressure") lateinit var pressure: String
    @ColumnInfo(name = "temp") lateinit var temp: String
    @ColumnInfo(name = "humidity") lateinit var humidity: String
    @ColumnInfo(name = "wind") lateinit var wind: String
    @ColumnInfo(name = "cloud") lateinit var cloud: String

    override fun toString(): String {
        return Gson().toJson(this).toString()
    }
}