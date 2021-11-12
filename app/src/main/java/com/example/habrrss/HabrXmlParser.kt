package com.example.habrrss

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws


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
                Timber.d("attempt to read item")
                entries.add(readItem(parser))
                Timber.d("item = ${entries.last()}")
            } else {
                Timber.d("attempt to skip")
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


//        var link: String? = null
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
//
//    @Throws(XmlPullParserException::class, IOException::class)
//    private fun readCategories(parser: XmlPullParser): List<Category> {
//        val entries = mutableListOf<Category>()
//
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType != XmlPullParser.START_TAG) {
//                continue
//            }
//            if (parser.name == FeedItemContract.CATEGORY) {
//                entries.add(Category(parser.text))
//            } else {
//                Timber.d("attempt to skip")
//                skip(parser)
//            }
//        }
//        return entries
//    }


    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser, tag: String): String {
        Timber.d("try to read $tag")
        parser.require(XmlPullParser.START_TAG, ns, tag)
        Timber.d("start tag")
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            Timber.d("in if")
            result = parser.text
            Timber.d("result written")
            parser.nextTag()
            Timber.d("next tag passed")
        }
        Timber.d("readen")
        parser.require(XmlPullParser.END_TAG, ns, tag)
        Timber.d("end tag")
        Timber.d("success read $tag")
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException("Not start tag!!")
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

//
//    @Throws(XmlPullParserException::class, IOException::class)
//    private fun readEntry(parser: XmlPullParser): Entry {
//        parser.require(XmlPullParser.START_TAG, ns, "entry")
//        var title: String? = null
//        var summary: String? = null
//        var link: String? = null
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType != XmlPullParser.START_TAG) {
//                continue
//            }
//            when (parser.name) {
//                "title" -> title = readTitle(parser)
//                "summary" -> summary = readSummary(parser)
//                "link" -> link = readLink(parser)
//                else -> skip(parser)
//            }
//        }
//        return Entry(title, summary, link)
//    }
//
//    @Throws(IOException::class, XmlPullParserException::class)
//    private fun readTitle(parser: XmlPullParser): String {
//        parser.require(XmlPullParser.START_TAG, ns, "title")
//        val title = readText(parser)
//        parser.require(XmlPullParser.END_TAG, ns, "title")
//        return title
//    }
//
//    @Throws(IOException::class, XmlPullParserException::class)
//    private fun readLink(parser: XmlPullParser): String {
//        var link = ""
//        parser.require(XmlPullParser.START_TAG, ns, "link")
//        val tag = parser.name
//        val relType = parser.getAttributeValue(null, "rel")
//        if (tag == "link") {
//            if (relType == "alternate") {
//                link = parser.getAttributeValue(null, "href")
//                parser.nextTag()
//            }
//        }
//        parser.require(XmlPullParser.END_TAG, ns, "link")
//        return link
//    }
//
//    @Throws(IOException::class, XmlPullParserException::class)
//    private fun readSummary(parser: XmlPullParser): String {
//        parser.require(XmlPullParser.START_TAG, ns, "summary")
//        val summary = readText(parser)
//        parser.require(XmlPullParser.END_TAG, ns, "summary")
//        return summary
//    }
}