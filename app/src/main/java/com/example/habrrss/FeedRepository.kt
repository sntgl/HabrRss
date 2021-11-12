package com.example.habrrss

import com.example.habrrss.Networking.okhttpClient
import com.example.habrrss.Networking.parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class FeedRepository {
    private fun getRequest(): Request =
        Request.Builder().url("https://habr.com/ru/rss/hubs/all/").build()

    @Throws(XmlPullParserException::class, IOException::class, IllegalStateException::class)
    suspend fun getRss(): List<FeedItem>? {
        runCatching {
            val successStream = getStream().getOrThrow() ?: return null
            parser.parse(successStream)
        }.onSuccess { return it
        }.onFailure { throw it }
        return null
    }

    @Throws(IOException::class, IllegalStateException::class)
    private suspend fun getStream(): Result<InputStream?> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val response = okhttpClient.newCall(getRequest()).execute()
                if (!response.isSuccessful) throw IOException()
                return@withContext response.body?.byteStream()
            }
        }
    }


}