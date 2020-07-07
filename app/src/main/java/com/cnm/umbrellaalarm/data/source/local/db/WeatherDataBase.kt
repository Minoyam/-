package com.cnm.umbrellaalarm.data.source.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        private var INSTANCE: WeatherDataBase? = null

        fun getInstance(context: Context): WeatherDataBase? {
            if (INSTANCE == null) {
                synchronized(WeatherDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDataBase::class.java,
                        "db.db"
                    ).addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            val r = Runnable {
                                getInstance(context)?.weatherDao()?.insertLocal(
                                    WeatherEntity(
                                        0,
                                        "서울시 중구",
                                        "37.5577074",
                                        "126.9766557"
                                    )
                                )
                            }
                            val thread = Thread(r)
                            thread.start()
                        }
                    })
                        .build().also { INSTANCE = it }
                }
            }
            return INSTANCE
        }
    }

}


