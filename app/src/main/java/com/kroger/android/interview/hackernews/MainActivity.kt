package com.kroger.android.interview.hackernews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kroger.android.interview.hackernews.databinding.ActivityMainBinding
import com.kroger.android.interview.hackernews.repository.HackerNewsRepository
import com.kroger.android.interview.hackernews.viewmodels.MainViewModel
import com.kroger.android.interview.hackernews.viewmodels.MainViewModelFactory

/**
 * This Activity shows the Hacker News home screen
 *
 * The Challenge:
 *
 * Recreate the Hacker News home screen in the app. The UI can be simple.
 *
 * For reference: https://news.ycombinator.com/
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var app: HackerNewsApp
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as HackerNewsApp

        setupViewModel()

        viewModel.fetchTopStories()
    }

    private fun setupViewModel() {
        val factory = MainViewModelFactory(HackerNewsRepository(app.hackerNewsService))
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }
}
