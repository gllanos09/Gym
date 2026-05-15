package com.tecsup.gymtrackerpro.data.remote.api

import com.tecsup.gymtrackerpro.data.remote.model.ExerciseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WgerApiService {

    @GET("api/v2/exerciseinfo/")
    suspend fun getEjercicios(
        @Query("language") language: Int = 2,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ExerciseResponse
}