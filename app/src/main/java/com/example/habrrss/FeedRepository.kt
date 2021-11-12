package com.example.habrrss

import com.example.habrrss.Networking.okhttpClient
import com.example.habrrss.Networking.parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException
import kotlin.jvm.Throws

class FeedRepository {
    private fun getRequest(): Request =
        Request.Builder().url("https://habr.com/ru/rss/hubs/all/").build()

    @Throws(XmlPullParserException::class, IOException::class, IllegalStateException::class)
    suspend fun getRss(): List<FeedItem>? {
        val successStream = getStream().getOrThrow() ?: return null
        runCatching { parser.parse(successStream)
        }.onSuccess { return it
        }.onFailure { throw it }
        return null
    }

    private suspend fun getStream(): Result<InputStream?> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val response = okhttpClient.newCall(getRequest()).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                Timber.d("Body: ${response.body}")
                return@withContext response.body?.byteStream()
            }
        }
    }


}