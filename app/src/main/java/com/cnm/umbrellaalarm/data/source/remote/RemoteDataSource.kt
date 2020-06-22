package com.cnm.umbrellaalarm.data.source.remote

import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.model.WeatherResponse
import io.reactivex.Single

interface RemoteDataSource {
    fun getWeather(exclude: String, lat: String, lon: String): Single<WeatherResponse>

    fun getAddress(query: String): Single<NaverGeocodeResponse>
}