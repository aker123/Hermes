package com.example.hermes.domain.data.network.registration

import com.example.hermes.domain.data.network.registration.models.IUser
import retrofit2.Call
import retrofit2.http.*


interface IRegistrationApi {

    @POST("users")
    fun setUser(@Body user: IUser?): Call<IUser?>?
}