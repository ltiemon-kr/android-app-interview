package com.kroger.android.interview.hackernews.repository

import com.kroger.android.interview.hackernews.network.HackerNewsService
import com.kroger.android.interview.hackernews.network.Item
import io.reactivex.Single

/**
 *  Repository for fetching hacker news data
 */
class HackerNewsRepository(private val hackerNewsService: HackerNewsService) {
    /**
     * If the candidate wants to use RxJava
     */
    fun getTopStoriesRx(): Single<List<Int>> = hackerNewsService.getTopStoriesRx()

    fun getItemRx(id: Int): Single<Item> = hackerNewsService.getItemRx(id)

    /**
     * If the candidate wants to use coroutines
     */
    suspend fun getTopStories(): List<Int> = hackerNewsService.getTopStories()

    suspend fun getItem(id: Int): Item = hackerNewsService.getItem(id)
}
