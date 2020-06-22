package com.cnm.umbrellaalarm.data.source.local

import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherEntity

class LocalDataSourceImpl(private val dao: WeatherDao) : LocalDataSource {
    override fun saveWeather(weatherEntity: WeatherEntity) = dao.insertLocal(weatherEntity)

    override fun loadWeather(): WeatherEntity = dao.loadLocal()

}