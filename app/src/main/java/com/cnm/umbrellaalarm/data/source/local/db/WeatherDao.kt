package com.cnm.umbrellaalarm.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocal(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather ")
    fun loadLocal(): WeatherEntity
}