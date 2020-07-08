package com.cnm.umbrellaalarm.ui.main

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cnm.umbrellaalarm.R
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDataBase
import com.cnm.umbrellaalarm.databinding.ActivityMainBinding
import com.cnm.umbrellaalarm.receiver.AlarmReceiver
import com.cnm.umbrellaalarm.receiver.AlarmService
import com.cnm.umbrellaalarm.ui.address.AddressActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private val weatherDao: WeatherDao by lazy {
        val db = WeatherDataBase.getInstance(this)!!
        db.weatherDao()
    }
    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(weatherDao) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        moveAddress()
        binding.btReservation.setOnClickListener { notiReservation() }
    }


    private fun loadWeather() {
        val r = Runnable {
            val entity = viewModel.loadLocal()
            viewModel.loadWeather(entity.address, entity.latitude, entity.longitude)
        }
        val thread = Thread(r)
        thread.start()

    }

    private fun moveAddress() {
        viewModel.isMoveAddress.observe(this@MainActivity, Observer {
            if (it) {
                val intent = Intent(this, AddressActivity::class.java)
                startActivityForResult(intent, 1001)
            }
        })
    }

    fun notiReservation() {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            AlarmService.NOTIFICATION_ID, alarmIntent,
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
        Toast.makeText(this, "알람 예약 완료", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                val latitude = data?.getStringExtra("latitude") as String
                val longitude = data.getStringExtra("longitude") as String
                val roadName = data.getStringExtra("roadName") as String
                viewModel.loadWeather(roadName, latitude, longitude)
            }
        }
    }

    private fun initActivity() {
        with(binding) {
            lifecycleOwner = this@MainActivity
            this.vm = viewModel
            if (viewModel.weatherItem.value == null) {
                loadWeather()
            }
        }
    }
}
