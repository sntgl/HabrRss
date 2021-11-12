package com.example.habrrss

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream


class HabrXmlParser {

    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(stream: InputStream): List<FeedItem> {
        stream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<FeedItem> {
        val entries = mutableListOf<FeedItem>()

        parser.require(XmlPullParser.START_TAG, ns, FeedItemContract.RSS)
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, ns, FeedItemContract.CHANNEL)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == FeedItemContract.ITEM) {
                entries.add(readItem(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItem(parser: XmlPullParser): FeedItem {
        parser.require(XmlPullParser.START_TAG, ns, FeedItemContract.ITEM)
        var title: String? = null
        var description: String? = null
        var link: String? = null
        var author: String? = null
        var date: String? = null
        val categories = mutableListOf<Category>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                FeedItemContract.TITLE ->
                    title = readText(parser, FeedItemContract.TITLE)
                FeedItemContract.DESCRIPTION ->
                    description = readText(parser, FeedItemContract.DESCRIPTION)
                FeedItemContract.LINK ->
                    link = readText(parser, FeedItemContract.LINK)
                FeedItemContract.AUTHOR ->
                    author = readText(parser, FeedItemContract.AUTHOR)
                FeedItemContract.DATE ->
                    date = readText(parser, FeedItemContract.DATE)
                FeedItemContract.CATEGORY ->
                    categories.add(Category(readText(parser, FeedItemContract.CATEGORY)))
                else -> skip(parser)
            }
        }
        val feedItem = FeedItem(
            title = title,
            description = description,
            link = link,
            creator = author,
            date = date,
            categories = categories
        )
        return feedItem
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

}