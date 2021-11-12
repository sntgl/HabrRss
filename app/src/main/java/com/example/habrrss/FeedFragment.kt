package com.example.habrrss

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.habrrss.databinding.FeedFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class FeedFragment : Fragment(R.layout.feed_fragment) {


    val binding: FeedFragmentBinding by viewBinding(FeedFragmentBinding::bind)
    var feedAdapter: FeedAdapter? = null
    val feedViewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Creating feed adapter")
        feedAdapter = FeedAdapter()
        Timber.d("Created!!!!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        lifecycleScope.launch {
            feedViewModel.feedFlow.collect {
                binding.feedSwipe.isRefreshing = false
                feedAdapter?.newItems(it.toList())
                Timber.d("New items count = ${it.size}")
            }
        }

        lifecycleScope.launch {
            feedViewModel.feedError.collect {
                when (it) {
                    FeedError.OK -> binding.feedSwipe.isRefreshing = false
                    FeedError.NETWORKING -> doNetworkingErrorSnackBar()
                    FeedError.PARSING -> doParsingErrorSnackBar()
                }
            }
        }
    }

    private fun bindViews() {
        with(binding.feedRecycler) {
            val orientation = RecyclerView.VERTICAL
            layoutManager = LinearLayoutManager(context, orientation, false)
            addItemDecoration(DividerItemDecoration(context, orientation))
            adapter = feedAdapter
        }
        with(binding) {
            feedSwipe.setOnRefreshListener { feedViewModel.rss() }
            feedSwipe.isRefreshing = true
        }
    }

    private fun doNetworkingErrorSnackBar() {
        Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_LONG)
            .setAction(R.string.retry) {
                feedViewModel.rss()
            }
            .show()
    }

    private fun doParsingErrorSnackBar() {
        Snackbar.make(binding.root, R.string.parsing_error, Snackbar.LENGTH_LONG)
            .setAction(R.string.retry) {
                feedViewModel.rss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        feedAdapter = null
    }

}