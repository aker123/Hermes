package com.example.hermes.di

import android.annotation.SuppressLint
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
class RetrofitServiceModule {

    @Provides
    @Singleton
    @Named("provideRetrofitService")
    fun provideRetrofitFobos(
        @Named("provideOkHttpClient")
        okHttpClient: OkHttpClient,
        @Named("provideConverterFactory")
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.3.2:5075/api/")
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    @Named("provideOkHttpClient")
    fun provideOkHttpClient(
        @Named("provideHttpLoggingInterceptor")
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("provideHttpLoggingInterceptor")
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply { loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY }
        return loggingInterceptor
    }

    @Provides
    @Singleton
    @Named("provideConverterFactory")
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }
}