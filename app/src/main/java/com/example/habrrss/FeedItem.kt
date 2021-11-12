package com.example.habrrss

import java.io.FileDescriptor

data class FeedItem (
        val title: String?,
        val description: String?,
        val date: String?,
        val creator: String?,
        val link: String?,
        val categories: List<Category>?,
)

data class Category (
        val name: String
)

object FeedItemContract {
        const val RSS = "rss"
        const val CHANNEL = "channel"
        const val ITEM = "item"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val LINK = "link"
        const val DATE = "pubDate"
        const val AUTHOR = "dc:creator"
        const val CATEGORY = "category"
}
