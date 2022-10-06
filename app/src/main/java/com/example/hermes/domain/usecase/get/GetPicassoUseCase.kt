package com.example.hermes.domain.usecase.get


import android.content.Context
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient

class GetPicassoUseCase(
    val context: Context,
    val picassoClient: OkHttpClient
) {

    fun execute(): Picasso{
        return Picasso.Builder(context).downloader(OkHttp3Downloader(picassoClient)).build()
    }
}