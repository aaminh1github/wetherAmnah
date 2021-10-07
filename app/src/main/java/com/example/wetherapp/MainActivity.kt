package com.example.wetherapp

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.net.URL
import java.sql.Time
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var tvCity: TextView
    lateinit var tvTime: TextView
    lateinit var tvDescript: TextView
    lateinit var tvTemp: TextView
    lateinit var tvLow: TextView
    lateinit var tvHigh: TextView
    lateinit var tvSunrise: TextView
    lateinit var tvSuset: TextView
    lateinit var tvWind: TextView
    lateinit var tvHumidity: TextView
    lateinit var ibRefresh: TextView
    lateinit var tvPressure: TextView

    private val APIbs = "1cc2d66d90916672e8843626b8957be5\n"
    private val city = "10001"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvCity = findViewById(R.id.tvCity)
        tvTime = findViewById(R.id.tvTime)
        tvDescript = findViewById(R.id.tvDescrip)
        tvTemp = findViewById(R.id.tvTemp)
        tvLow = findViewById(R.id.tvLow)
        tvHigh = findViewById(R.id.tvHigh)
        tvSunrise = findViewById(R.id.tvSunrise)
        tvSuset = findViewById(R.id.tvSunset)
        tvWind = findViewById(R.id.tvWind)
        tvHumidity = findViewById(R.id.tvHumidity)
        ibRefresh = findViewById(R.id.ibRefresh)
        tvPressure = findViewById(R.id.tvPrus)


        requistAPI()
ibRefresh.setOnClickListener {
    this.recreate()
}
    }

    private fun requistAPI() {

        CoroutineScope(IO).launch {
            val data =async{ fetchData() }.await()
            if (data.isNotEmpty()) {
                Log.d("TAG",data)
                populateData(data)
            }

        }
    }

    private fun fetchData(): String {
        var response = " "
        try {
            response = URL("https://api.openweathermap.org/data/2.5/weather?zip=$city,us&appid=$APIbs").readText(Charsets.UTF_8)

        } catch (e: Exception) {
            Log.d("Main", "Issue:$e")
        }
        return response
    }

    private suspend fun populateData(result: String) {
        withContext(Dispatchers.Main) {
        val jsonObject = JSONObject(result)

        val sys = jsonObject.getJSONObject("sys")
        val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
        val main = jsonObject.getJSONObject("main")
            val wind=jsonObject.getJSONObject(("wind"))

        val cityName = jsonObject.getString("name")
        tvCity.text = cityName + "," + sys.getString("country")

        val lastUpdate :Long=jsonObject.getLong("dt")
        val lastUpdateText="Update at: "+SimpleDateFormat("dd/MM/yyyy hh:mm a",Locale.ENGLISH)
            .format(Date(lastUpdate*1000))
            tvTime.text=lastUpdateText

        val descrip =weather.getString("main")
            tvDescript.text=descrip

            val Low =main.getString("temp_min")
            tvLow.text="Low $Low F"

            val High =main.getString("temp_max")
            tvHigh.text="High $High F"

            val Tempr =main.getString("temp")
            tvTemp.text="$Tempr F"

            val sunrise=sys.getLong("sunrise")
            tvSunrise.text="Sunchine "+SimpleDateFormat("hh:mm a",Locale.ENGLISH)
                .format(Date(sunrise*1000))
            val sunset=sys.getLong("sunset")
             tvSunset.text="Sunset "+SimpleDateFormat("hh:mm a",Locale.ENGLISH)
             .format(Date(sunset*1000))




           val Wind =wind.getString("speed")
            tvWind.text="$Wind km/h"
           val Humidity=main.getString("humidity")
            tvHumidity.text=Humidity
           val Pressure=main.getString("pressure")
            tvPressure.text=Pressure


}

        }
}
