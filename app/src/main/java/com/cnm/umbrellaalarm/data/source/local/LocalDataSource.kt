package com.cnm.umbrellaalarm.data.source.local

import com.cnm.umbrellaalarm.data.source.local.db.WeatherEntity

interface LocalDataSource {
    fun saveWeather(weatherEntity: WeatherEntity)

    fun loadWeather() : WeatherEntity
}