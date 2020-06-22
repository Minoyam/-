package com.cnm.umbrellaalarm.data.source.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey
    var id: Int,
    var address: String,
    var latitude: String,
    var longitude: String
)