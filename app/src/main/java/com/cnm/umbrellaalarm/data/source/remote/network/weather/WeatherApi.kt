package com.cnm.umbrellaalarm.data.source.remote.network.weather

import com.cnm.umbrellaalarm.data.model.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/onecall")
    fun getWeather(
        @Query("exclude") exclude: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = "3c5f20a2c52a36830008c3a23af45ff8"
    ): Single<WeatherResponse>
}