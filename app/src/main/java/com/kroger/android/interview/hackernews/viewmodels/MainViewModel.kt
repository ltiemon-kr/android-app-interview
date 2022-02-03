package com.kroger.android.interview.hackernews.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kroger.android.interview.hackernews.repository.HackerNewsRepository

/**
 * ViewModel for [com.kroger.android.interview.hackernews.MainActivity]
 */
class MainViewModel(private val hackerNewsRepository: HackerNewsRepository) : ViewModel() {
    fun fetchTopStories() {
        // TODO: The candidate implements this
    }
}

class MainViewModelFactory(
    private val hackerNewsRepository: HackerNewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HackerNewsRepository::class.java)
            .newInstance(hackerNewsRepository)
    }
}
