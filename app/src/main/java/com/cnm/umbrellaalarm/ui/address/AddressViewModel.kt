package com.cnm.umbrellaalarm.ui.address

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.repository.RepositoryImpl
import com.cnm.umbrellaalarm.data.source.local.LocalDataSourceImpl
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSourceImpl
import io.reactivex.disposables.CompositeDisposable

class AddressViewModel (private val weatherDao: WeatherDao) : ViewModel(){

    private val repositoryImpl : RepositoryImpl  by lazy {
        RepositoryImpl(
            RemoteDataSourceImpl(),
            LocalDataSourceImpl(weatherDao)
        )
    }
    private val disposable = CompositeDisposable()

    val geocodeItem = MutableLiveData<List<NaverGeocodeResponse.Addresse>>()
    val searchString = MutableLiveData<String>()
    val toastString = MutableLiveData<String>()

    fun searchAddress() {
        val query = searchString.value as String
        if(query.isNotEmpty()) {
            disposable.add(
                repositoryImpl.getAddress(query)
                    .subscribe({
                        if(it.addresses.isEmpty())
                            toastString.postValue("검색 결과가 없습니다.")
                        setItems(it.addresses)
                    }, {
                        Log.e("disposable", it.message.toString())
                    })
            )
        }
    }
    private fun setItems(it: List<NaverGeocodeResponse.Addresse>) {
        geocodeItem.postValue(null)
        geocodeItem.postValue(it)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}