package com.cnm.umbrellaalarm.data.source.remote.network.navergeocode

import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverApi {
    @GET("map-geocode/v2/geocode")
    fun getAddress(@Query("query") query: String): Single<NaverGeocodeResponse>


}