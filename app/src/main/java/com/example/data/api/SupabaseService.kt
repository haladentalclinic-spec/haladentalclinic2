package com.example.data.api

import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface SupabaseService {

    @GET("rest/v1/clinics")
    suspend fun getClinics(): List<Clinic>

    @GET("rest/v1/users")
    suspend fun getUsers(
        @Query("role") roleFilter: String? = null,
        @Query("id") idFilter: String? = null
    ): List<UserData>

    @POST("rest/v1/users")
    suspend fun createUser(
        @Body user: UserData,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<UserData>

    @PATCH("rest/v1/users")
    suspend fun updateUser(
        @Query("id") idFilter: String,
        @Body user: UserData
    ): List<UserData>

    @GET("rest/v1/works")
    suspend fun getWorks(): List<Work>

    @POST("rest/v1/works")
    suspend fun createWork(
        @Body work: Work,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<Work>

    @GET("rest/v1/reminders")
    suspend fun getReminders(): List<Reminder>

    @POST("rest/v1/reminders")
    suspend fun createReminder(
        @Body reminder: Reminder,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<Reminder>

    @GET("rest/v1/banners")
    suspend fun getBanners(): List<BannerInfo>

    @GET("rest/v1/settings")
    suspend fun getSettings(): List<ClinicSetting>
}

object SupabaseClient {
    // Falls back gracefully if BuildConfig keys are missing, using your provided values
    const val DEFAULT_URL = "https://qmssimnsohijqhbshfjn.supabase.co/"
    const val DEFAULT_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFtc3NpbW5zb2hpanFoYnNoZmpuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk4MTY5ODAsImV4cCI6MjA5NTM5Mjk4MH0.5GZMjqh43EXZcg1VwtJ5Gk0zkQTExMaPTUpxiGgcPf8"

    private inline fun <reified T> getBuildConfigValue(fieldName: String, defaultValue: T): T {
        return try {
            val buildConfigClass = Class.forName("com.example.BuildConfig")
            val field = buildConfigClass.getField(fieldName)
            field.get(null) as T
        } catch (e: Exception) {
            defaultValue
        }
    }

    val supabaseUrl: String
        get() {
            var url = getBuildConfigValue("SUPABASE_URL", DEFAULT_URL)
            if (!url.endsWith("/")) {
                url += "/"
            }
            return url
        }

    val supabaseKey: String
        get() = getBuildConfigValue("SUPABASE_ANON_KEY", DEFAULT_KEY)

    fun createService(): SupabaseService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val headerInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("apikey", supabaseKey)
                .header("Authorization", "Bearer $supabaseKey")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")

            chain.proceed(requestBuilder.build())
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(supabaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(SupabaseService::class.java)
    }
}
