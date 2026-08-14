package com.mvl.locationassignment.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mvl.locationassignment.data.api.BookingApiService
import com.mvl.locationassignment.data.api.WaqiApiService
import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WaqiRetrofit

private const val TAG = "NetworkModule"

class MockBookingInterceptor(private val gson: Gson) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method
        
        Log.d(TAG, "════════════════════════════════════════════════════════════════════════════════")
        Log.d(TAG, "🔍 INTERCEPTED REQUEST: $method $url")
        Log.d(TAG, "════════════════════════════════════════════════════════════════════════════════")

        return if (method == "POST" && url.contains("books")) {
            Log.d(TAG, "✅ MATCHED BOOKING POST REQUEST!")
            
            val requestBody = request.body?.let {
                val bytes = okio.Buffer()
                it.writeTo(bytes)
                bytes.readUtf8()
            } ?: ""

            Log.d(TAG, "📤 REQUEST BODY:")
            Log.d(TAG, requestBody)
            
            // Simulate 2-second processing delay
            Log.d(TAG, "⏳ Processing for 2 seconds...")
            Thread.sleep(2000)
            Log.d(TAG, "✅ Processing complete")
            
            // Parse request to create mock response
            val bookingRequest = try {
                gson.fromJson(requestBody, BookingRequest::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to parse booking request: ${e.message}", e)
                return Response.Builder()
                    .code(400)
                    .message("Bad Request")
                    .protocol(Protocol.HTTP_1_1)
                    .request(request)
                    .body("Bad Request".toResponseBody("text/plain".toMediaType()))
                    .build()
            }

            // Create mock response
            val mockResponse = BookingResponse(
                a = bookingRequest.a.copy(),
                b = bookingRequest.b.copy(),
                price = 10000
            )

            val responseBody = gson.toJson(mockResponse)
            Log.d(TAG, "📥 RESPONSE BODY:")
            Log.d(TAG, responseBody)
            Log.d(TAG, "════════════════════════════════════════════════════════════════════════════════\n")

            Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(responseBody.toResponseBody("application/json".toMediaType()))
                .addHeader("content-type", "application/json")
                .build()
        } else {
            Log.d(TAG, "⏭️  NOT A BOOKING REQUEST - Passing to next interceptor")
            Log.d(TAG, "════════════════════════════════════════════════════════════════════════════════\n")
            chain.proceed(request)
        }
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
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, gson: Gson): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(MockBookingInterceptor(gson))  // Mock booking requests
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
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
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
