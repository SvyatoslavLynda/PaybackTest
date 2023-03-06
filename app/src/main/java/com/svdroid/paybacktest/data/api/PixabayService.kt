package com.svdroid.paybacktest.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayService {
    @GET("/api/?key=34044235-823ce13b733d0e892d02e95aa&pretty=true&per_page=100")
    suspend fun getAllHits(@Query("q") query: String = "fruits", @Query("page") page: Int): PixabayResponse
}