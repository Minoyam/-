package com.cnm.umbrellaalarm.receiver

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cnm.umbrellaalarm.R
import com.cnm.umbrellaalarm.data.model.WeatherResponse
import com.cnm.umbrellaalarm.data.repository.RepositoryImpl
import com.cnm.umbrellaalarm.data.source.local.LocalDataSourceImpl
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDataBase
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSourceImpl
import com.cnm.umbrellaalarm.ui.main.MainActivity
import io.reactivex.disposables.CompositeDisposable

class AlarmService : Service() {


    private val disposable = CompositeDisposable()
    private val weatherDao: WeatherDao by lazy {
        val db = WeatherDataBase.getInstance(this)!!
        db.weatherDao()
    }
    private val repositoryImpl: RepositoryImpl by lazy {
        RepositoryImpl(
            RemoteDataSourceImpl(),
            LocalDataSourceImpl(weatherDao)
        )
    }
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val NOTIFICATION_ID = 1
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, Notification.Builder(this).build())
        }

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val r = Runnable {
            val entity = weatherDao.loadLocal()
            disposable.add(
                repositoryImpl.getWeather(entity.address, "daily", entity.latitude, entity.longitude)
                    .subscribe({
                        val item : List<WeatherResponse.Hourly> = it.hourly
                        var isSun = true
                        for(i in 0..15){
                            if(item[i].weather[0].main == "Rain"){
                                deliverNotification(this, "오늘 비와. 우산챙기세요.")
                                isSun = false
                                break
                            }
                            else if(item[i].weather[0].main == "Snow"){
                                deliverNotification(this, "오늘 눈와. 우산챙기세요.")
                                isSun = false
                                break
                            }
                        }
                        if(isSun){
                            deliverNotification(this, "오늘 안와. 우산없어도돼요.")
                        }

                    }, {
                        Log.e("disposable", it.message.toString())
                    })
            )

        }
        val thread = Thread(r)
        thread.start()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
    private fun deliverNotification(context: Context, text :String) {
        notificationManager = this.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(context,
                PRIMARY_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_umbrella)
                .setContentText(text)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "UmbrellaAlarm",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "UmbrellaAlarm"
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
    }
}