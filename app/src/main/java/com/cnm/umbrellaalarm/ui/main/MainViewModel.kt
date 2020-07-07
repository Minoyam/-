package com.cnm.umbrellaalarm.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cnm.umbrellaalarm.data.model.WeatherResponse
import com.cnm.umbrellaalarm.data.repository.RepositoryImpl
import com.cnm.umbrellaalarm.data.source.local.LocalDataSourceImpl
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherEntity
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSourceImpl
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(private val weatherDao: WeatherDao) : ViewModel() {

    private val repositoryImpl: RepositoryImpl by lazy {
        RepositoryImpl(
            RemoteDataSourceImpl(),
            LocalDataSourceImpl(weatherDao)
        )
    }
    private val disposable = CompositeDisposable()

    val weatherItem = MutableLiveData<List<WeatherResponse>>()
    val addressName = MutableLiveData<String>()
    val isMoveAddress = MutableLiveData<Boolean>()
    fun loadLocal(): WeatherEntity = weatherDao.loadLocal()

    fun loadWeather(roadName: String, latitude: String, longitude: String) {
        disposable.add(
            repositoryImpl.getWeather(roadName, "daily", latitude, longitude)
                .subscribe({
                    setItem(it)
                    setAddress(roadName)
                }, {
                    Log.e("disposable", it.message.toString())
                    weatherItem.value = null
                })
        )
    }

    private fun setAddress(roadName: String) {
        addressName.postValue(null)
        addressName.postValue(roadName)
    }

    private fun setItem(it: WeatherResponse) {
        weatherItem.postValue(null)
        weatherItem.postValue(mutableListOf(it))
    }

    fun moveAddress() {
        isMoveAddress.value = true
        isMoveAddress.value = false
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}