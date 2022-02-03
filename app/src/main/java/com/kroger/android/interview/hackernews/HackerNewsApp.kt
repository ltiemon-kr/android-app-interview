package com.kroger.android.interview.hackernews

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kroger.android.interview.hackernews.network.BASE_URL
import com.kroger.android.interview.hackernews.network.HackerNewsService
import com.kroger.android.interview.hackernews.network.local.DefaultKrogerFileLoader
import com.kroger.android.interview.hackernews.network.local.LocalHackerNewsService
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Custom Application that initiates basic framework dependencies
 */
class HackerNewsApp : Application() {
    lateinit var retrofit: Retrofit
        private set
    lateinit var hackerNewsService: HackerNewsService
        private set

    private val TIMEOUTSECONDS: Long = 10
    private val GSON_DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSSZ"

    override fun onCreate() {
        super.onCreate()
        initializeRetrofit()
        initService(BuildConfig.USE_LOCAL_DATA)
    }

    private fun initService(useLocalData: Boolean = true) {
        hackerNewsService = if (useLocalData) {
            LocalHackerNewsService(DefaultKrogerFileLoader(this))
        } else {
            retrofit.create(HackerNewsService::class.java)
        }
    }

    private fun initializeRetrofit() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUTSECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUTSECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUTSECONDS, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val callAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
        val gson = GsonBuilder().setDateFormat(GSON_DATE_FORMAT)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val gsonConverterFactory = GsonConverterFactory.create(gson)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }
}
