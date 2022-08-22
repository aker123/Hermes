package com.example.hermes.domain.data.network.authorization

import com.example.hermes.domain.data.network.authorization.models.IOperator
import com.example.hermes.domain.data.network.authorization.models.IUser

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IAuthorizationApi {

    @GET("operators")
    fun getOperator(
        @Query("login") login: String,
        @Query("password") password: String
    ): Call<IOperator>

    @GET("users")
    fun getUser(
        @Query("login") login: String,
        @Query("password") password: String
    ): Call<IUser>
}