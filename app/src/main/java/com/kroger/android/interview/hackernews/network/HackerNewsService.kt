package com.kroger.android.interview.hackernews.network

import android.os.Parcelable
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import retrofit2.http.GET
import retrofit2.http.Path

val BASE_URL = "https://hacker-news.firebaseio.com/"

/**
 * Retrofit service for the Hacker News API
 *
 * https://github.com/HackerNews/API
 */
interface HackerNewsService {
    /**
     * If the candidate wants to use RxJava
     */
    // e.g. https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty
    @GET("v0/topstories.json")
    fun getTopStoriesRx(): Single<List<Int>>

    // e.g. https://hacker-news.firebaseio.com/v0/item/26068032.json?print=pretty
    @GET("v0/item/{id}.json")
    fun getItemRx(@Path("id") id: Int): Single<Item>

    /**
     * If the candidate wants to use coroutines
     */
    @GET("v0/topstories.json")
    suspend fun getTopStories(): List<Int>

    @GET("v0/item/{id}.json")
    suspend fun getItem(@Path("id") id: Int): Item
}

@Parcelize
data class Item(
    val id: Int,
    val title: String,
    val text: String?,
    val time: Int,
    val type: String,
    val by: String?,
    val descendants: Int?,
    val kids: List<Int>?,
    val score: Int?,
    val url: String?
) : Parcelable
