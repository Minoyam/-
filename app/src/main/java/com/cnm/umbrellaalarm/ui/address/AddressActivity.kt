package com.cnm.umbrellaalarm.ui.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cnm.umbrellaalarm.R
import com.cnm.umbrellaalarm.adapter.AddressAdapter
import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.repository.RepositoryImpl
import com.cnm.umbrellaalarm.data.source.local.LocalDataSourceImpl
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDataBase
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSourceImpl
import com.cnm.umbrellaalarm.databinding.ActivityAddressBinding
import io.reactivex.disposables.CompositeDisposable

class AddressActivity : AppCompatActivity() {
    private val weatherDao: WeatherDao by lazy {
        val db = WeatherDataBase.getInstance(this)!!
        db.weatherDao()
    }
    private val addressViewModel : AddressViewModel by viewModels {
           object :  ViewModelProvider.Factory {
               override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                   return AddressViewModel(weatherDao) as T
               }
           }
    }
    private val addressAdapter = AddressAdapter(::selectAddress)

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityAddressBinding>(this,
            R.layout.activity_address
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
    }

    private fun initActivity() {
        with(binding)
        {
            rvAddressContent.apply {
                adapter = addressAdapter
                layoutManager = LinearLayoutManager(this@AddressActivity)
            }
            lifecycleOwner = this@AddressActivity
            vm = addressViewModel
        }
    }

    private fun selectAddress(item : NaverGeocodeResponse.Addresse)
    {
        val intent = Intent()
        intent.putExtra("roadName",item.jibunAddress)
        intent.putExtra("longitude",item.x)
        intent.putExtra("latitude",item.y)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}

