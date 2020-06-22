package com.cnm.umbrellaalarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cnm.umbrellaalarm.data.repository.RepositoryImpl
import com.cnm.umbrellaalarm.data.source.local.LocalDataSourceImpl
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDataBase
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSourceImpl
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val weatherDao: WeatherDao by lazy {
        val db = WeatherDataBase.getInstance(this)!!
        db.weatherDao()
    }
    private val repositoryImpl: RepositoryImpl by lazy {
        RepositoryImpl(RemoteDataSourceImpl(), LocalDataSourceImpl(weatherDao))
    }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (tv_address.text.isEmpty()) {
            val r = Runnable {
                weatherDao.loadLocal().apply {
                    searchWeather(this.address, this.latitude, this.longitude)
                }
            }
            val thread = Thread(r)
            thread.start()
        }
        tv_address.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivityForResult(intent, 1001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                val latitude = data?.getStringExtra("latitude") as String
                val longitude = data.getStringExtra("longitude") as String
                searchWeather(data.getStringExtra("roadName"), latitude, longitude)
            }
        }
    }

    private fun searchWeather(
        roadName: String,
        latitude: String,
        longitude: String
    ) {
        var currentWeather = ""
        var temp = 0

        disposable.add(
            repositoryImpl.getWeather(roadName, "daily", latitude, longitude)
                .doAfterSuccess {
                    val r = Runnable {
                        runOnUiThread {
                            when (currentWeather) {
                                "Clear" -> {
                                    iv_weather.setImageResource(R.drawable.ic_clear)
                                }
                                "Clouds" -> {
                                    iv_weather.setImageResource(R.drawable.ic_cloud)
                                }
                                "Snow" -> {
                                    iv_weather.setImageResource(R.drawable.ic_snow)
                                }
                                "Rain", "Drizzle", "Thunderstorm" -> {
                                    iv_weather.setImageResource(R.drawable.ic_rain)
                                }
                                else -> {
                                    iv_weather.setImageResource(R.drawable.ic_fog)
                                }
                            }
                            tv_address.text = roadName
                            tv_temp.text = temp.toString()
                            tv_do.text = "도"
                        }
                    }
                    val thread = Thread(r)
                    thread.start()
                }
                .subscribe({
                    currentWeather = it.current.weather[0].main
                    temp = it.current.temp.roundToInt()
                    for (i in 0..24) {
                        Log.e("!", it.hourly[i].weather[0].main)
                        Log.e("!", it.hourly[i].weather.toString())
                        Log.e("!", it.hourly[i].dt.toString())
                    }
                }, {
                    Log.e("disposable", it.message.toString())
                }
                )
        )
    }
}



