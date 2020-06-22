package com.cnm.umbrellaalarm.data.source.remote

import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.model.WeatherResponse
import com.cnm.umbrellaalarm.data.source.remote.network.navergeocode.NaverNetworkHelper
import com.cnm.umbrellaalarm.data.source.remote.network.weather.WeatherNetworkHelper
import io.reactivex.Single

class RemoteDataSourceImpl : RemoteDataSource {
    override fun getWeather(exclude: String, lat: String, lon: String): Single<WeatherResponse> =
        WeatherNetworkHelper.weatherApi.getWeather(exclude, lat, lon)

    override fun getAddress(query: String): Single<NaverGeocodeResponse> =
        NaverNetworkHelper.naverApi.getAddress(query)
}