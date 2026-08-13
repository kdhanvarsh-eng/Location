package com.mvl.locationassignment.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mvl.locationassignment.data.api.LocationApiService
import com.mvl.locationassignment.data.api.WaqiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WaqiRetrofit

private const val TAG = "NetworkModule"

/**
 * Custom interceptor for detailed request/response logging
 */
class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━ REQUEST ━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "🔗 URL: ${request.url}")
        Log.d(TAG, "📍 Method: ${request.method}")
        Log.d(TAG, "📦 Headers: ${request.headers}")
        
        if (request.body != null) {
            try {
                val buffer = Buffer()
                request.body!!.writeTo(buffer)
                Log.d(TAG, "📤 Body: ${buffer.readUtf8()}")
            } catch (e: Exception) {
                Log.d(TAG, "📤 Body: (unable to log)")
            }
        }
        
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime
        
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━ RESPONSE ━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "✅ Status: ${response.code} ${response.message}")
        Log.d(TAG, "⏱️  Duration: ${duration}ms")
        Log.d(TAG, "📥 Headers: ${response.headers}")
        
        val responseBody = response.peekBody(Long.MAX_VALUE)
        Log.d(TAG, "📥 Body: ${responseBody.string()}")
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        return response
    }
}

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
            .addInterceptor(RequestInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideLocationApiService(retrofit: Retrofit): LocationApiService {
        return retrofit.create(LocationApiService::class.java)
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
}
