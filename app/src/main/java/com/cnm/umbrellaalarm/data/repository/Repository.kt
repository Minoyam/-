package com.cnm.umbrellaalarm.data.repository

import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.model.WeatherResponse
import com.cnm.umbrellaalarm.data.source.local.db.WeatherEntity
import io.reactivex.Single

interface Repository {
    fun getWeather(
        roadName: String,
        exclude: String,
        lat: String,
        lon: String
    ): Single<WeatherResponse>

    fun getAddress(query: String): Single<NaverGeocodeResponse>

    fun saveWeather(weatherEntity: WeatherEntity)

    fun loadWeather(): WeatherEntity
}