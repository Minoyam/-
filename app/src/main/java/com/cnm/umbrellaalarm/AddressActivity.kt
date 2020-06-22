package com.cnm.umbrellaalarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cnm.umbrellaalarm.adapter.AddressAdapter
import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.repository.RepositoryImpl
import com.cnm.umbrellaalarm.data.source.local.LocalDataSourceImpl
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDao
import com.cnm.umbrellaalarm.data.source.local.db.WeatherDataBase
import com.cnm.umbrellaalarm.data.source.remote.RemoteDataSourceImpl
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_address.*


class AddressActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val weatherDao: WeatherDao by lazy {
        val db = WeatherDataBase.getInstance(this)!!
        db.weatherDao()
    }
    private val repositoryImpl: RepositoryImpl by lazy {
        RepositoryImpl(RemoteDataSourceImpl(),LocalDataSourceImpl(weatherDao))
    }
    private val addressAdapter = AddressAdapter(::selectAddress)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        rv_address_content.apply {
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(this@AddressActivity)
        }
        et_address.setOnEditorActionListener { _, i, _ ->
            when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    searchAddress()
                }
            }
            true
        }
        bt_address.setOnClickListener {
            searchAddress()
        }
    }

    private fun searchAddress() {
        disposable.add(
            repositoryImpl.getAddress(et_address.text.toString())
                .subscribe({
                    val r = Runnable {
                        runOnUiThread {
                            addressAdapter.setItem(it.addresses)
                        }
                    }
                    val thread = Thread(r)
                    thread.start()

                }, {
                    Log.e("disposable", it.message.toString())
                })
        )
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
    private fun selectAddress(item : NaverGeocodeResponse.Addresse)
    {
        val intent = Intent()
        intent.putExtra("roadName",item.roadAddress)
        intent.putExtra("longitude",item.x)
        intent.putExtra("latitude",item.y)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}

