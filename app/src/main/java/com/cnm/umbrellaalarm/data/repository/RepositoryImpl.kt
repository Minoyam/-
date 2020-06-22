package com.cnm.umbrellaalarm.data.repository

import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.model.WeatherResponse
import com.cnm.umbrellaalarm.data.source.local.LocalDataSource
import com.cnm.umbrellaalarm.data.source.local.db.WeatherEntity
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSource
import io.reactivex.Single

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : Repository {
    override fun getWeather(roadName : String,exclude: String, lat: String, lon: String): Single<WeatherResponse> =
        remoteDataSource.getWeather(exclude, lat, lon)
            .doOnSuccess {
                saveWeather(WeatherEntity(0,roadName,lat,lon))
            }

    override fun getAddress(query: String): Single<NaverGeocodeResponse> =
        remoteDataSource.getAddress(query)

    override fun saveWeather(weatherEntity: WeatherEntity) {
        localDataSource.saveWeather(weatherEntity)
    }
    override fun loadWeather() : WeatherEntity{
        return localDataSource.loadWeather()
    }


}