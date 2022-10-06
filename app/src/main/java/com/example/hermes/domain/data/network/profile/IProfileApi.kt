package com.example.hermes.domain.data.network.profile

import com.example.hermes.domain.data.network.profile.models.IOperator
import com.example.hermes.domain.data.network.profile.models.IUser
import retrofit2.Call
import retrofit2.http.*


interface IProfileApi {

    @POST("users")
    fun setUser(@Body user: IUser?): Call<IUser?>?

    @PUT("users")
    fun updateUser(@Body user: IUser?): Call<IUser?>?

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