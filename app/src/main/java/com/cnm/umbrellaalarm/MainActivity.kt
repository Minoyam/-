package com.cnm.umbrellaalarm

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cnm.umbrellaalarm.data.repository.RepositoryImpl
import com.cnm.umbrellaalarm.data.source.local.LocalDataSourceImpl
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDataBase
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSourceImpl
import com.cnm.umbrellaalarm.databinding.ActivityMainBinding
import io.reactivex.disposables.CompositeDisposable
import java.util.*
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

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this@MainActivity
        binding.ac = this
        if (binding.tvAddress.text.isEmpty()) {
            val r = Runnable {
                weatherDao.loadLocal().apply {
                    searchWeather(this.address, this.latitude, this.longitude)
                }
            }
            val thread = Thread(r)
            thread.start()
        }
    }

    fun moveAddress() {
        val intent = Intent(this, AddressActivity::class.java)
        startActivityForResult(intent, 1001)
    }

    fun notiReservation() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)  // 1
        val pendingIntent = PendingIntent.getBroadcast(     // 2
            this, AlarmReceiver.NOTIFICATION_ID, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar: Calendar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, binding.tpReservation.hour)
                set(Calendar.MINUTE, binding.tpReservation.minute)
            }
        } else {
            calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, binding.tpReservation.currentHour)
                set(Calendar.MINUTE, binding.tpReservation.currentMinute)
            }
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
         Toast.makeText(this,"알람 예약 완료",Toast.LENGTH_LONG).show()
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
                                    binding.ivWeather.setImageResource(R.drawable.ic_clear)
                                }
                                "Clouds" -> {
                                    binding.ivWeather.setImageResource(R.drawable.ic_cloud)
                                }
                                "Snow" -> {
                                    binding.ivWeather.setImageResource(R.drawable.ic_snow)
                                }
                                "Rain", "Drizzle", "Thunderstorm" -> {
                                    binding.ivWeather.setImageResource(R.drawable.ic_rain)
                                }
                                else -> {
                                    binding.ivWeather.setImageResource(R.drawable.ic_fog)
                                }
                            }
                            binding.tvAddress.text = roadName
                            binding.tvTemp.text = temp.toString() + "도"
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



