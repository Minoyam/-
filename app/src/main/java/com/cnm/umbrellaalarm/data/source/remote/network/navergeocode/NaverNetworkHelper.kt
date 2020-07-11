package com.cnm.umbrellaalarm.data.source.remote.network.navergeocode

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NaverNetworkHelper {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        })
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("X-NCP-APIGW-API-KEY-ID","e01jkwgzva")
                .addHeader("X-NCP-APIGW-API-KEY","HaPEPOyRYRzuKYXXS9Aiu62hReK1cJM7nXXnhGmV")
                .build()
            it.proceed(request)
        }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://naveropenapi.apigw.ntruss.com/")
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val naverApi: NaverApi = retrofit.create(NaverApi::class.java)
}