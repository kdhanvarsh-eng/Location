package com.mvl.locationassignment.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mvl.locationassignment.data.api.BookingApiService
import com.mvl.locationassignment.data.api.WaqiApiService
import com.mvl.locationassignment.network.MockWebServerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WaqiRetrofit

private const val TAG = "NetworkModule"


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // Log all requests/responses
            .build()
    }


    @Singleton
    @Provides
    @WaqiRetrofit
    fun provideWaqiRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideWaqiApiService(@WaqiRetrofit retrofit: Retrofit): WaqiApiService {
        return retrofit.create(WaqiApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMockWebServerManager(gson: Gson): MockWebServerManager {
        return MockWebServerManager(gson)
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson, mockWebServerManager: MockWebServerManager): Retrofit {
        // Assume MockWebServerManager is started by Application lifecycle
        val baseUrl = mockWebServerManager.getBaseUrl()
        Log.d(TAG, "Using MockWebServer base URL: $baseUrl")

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideBookingApiService(retrofit: Retrofit): BookingApiService {
        return retrofit.create(BookingApiService::class.java)
    }
}
