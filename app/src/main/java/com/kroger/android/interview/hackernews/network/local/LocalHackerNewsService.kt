package com.kroger.android.interview.hackernews.network.local

import com.kroger.android.interview.hackernews.network.HackerNewsService
import com.kroger.android.interview.hackernews.network.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single

/**
 * If Hacker News API is down or we want to start with a dummy data set, substitute the retrofit
 * instance of this interface.
 * IOW, set the USE_LOCAL_DATA BuildConfig property to true.
 */
class LocalHackerNewsService(private val fileLoader: KrogerFileLoader) : HackerNewsService {
    /**
     * @returns a static list of item Id's
     */
    override fun getTopStoriesRx(): Single<List<Int>> {
        return Single.just(listOf(1, 2, 3, 4, 5))
    }

    /**
     * @returns an Item for a particular Id fetched from disk. Returns a dummy Item if the id
     * could not be found
     */
    override fun getItemRx(id: Int): Single<Item> {
        return Single.just(getItemForId(id))
    }

    /**
     * @returns a static list of item Id's
     */
    override suspend fun getTopStories(): List<Int> {
        return listOf(1, 2, 3, 4, 5)
    }

    /**
     * @returns an Item for a particular Id fetched from disk. Returns a dummy Item if the id
     * could not be found
     */
    override suspend fun getItem(id: Int): Item {
        return getItemForId(id)
    }

    // Fetches the Item from the JSON file stored locally. It throws an IllegalArgumentException if
    // there is no matching Id
    private fun getItemForId(id: Int): Item {
        val jsonString = fileLoader.readFile("items.json")
        val listLocalData = object : TypeToken<LocalData>() {}.type
        val localData: LocalData = Gson().fromJson(jsonString, listLocalData)
        val item: Item? = localData.data.find { it.id == id }
        return item ?: throw IllegalArgumentException("id $id not recognized")
    }

    data class LocalData(
        var data: List<Item>
    )
}
