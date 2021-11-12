package com.example.habrrss

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.Html
import android.widget.TextView
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException

//какое-то убожество со stackoverflow

class URLDrawable : BitmapDrawable() {
    var drawable: Drawable? = null
    override fun draw(canvas: Canvas) {
        if (drawable != null) {
            drawable!!.draw(canvas)
        }
    }
}

class URLImageParser(var container: TextView) : Html.ImageGetter {
    override fun getDrawable(source: String): Drawable {
        val urlDrawable = URLDrawable()
        val asyncTask = ImageGetterAsyncTask(urlDrawable)
        asyncTask.execute(source)
        return urlDrawable
    }

    inner class ImageGetterAsyncTask(var urlDrawable: URLDrawable) :
        AsyncTask<String?, Void?, Drawable?>() {
        override fun doInBackground(vararg params: String?): Drawable {
            val source = params[0]
            return fetchDrawable(source!!)
        }

        override fun onPostExecute(result: Drawable?) {
            urlDrawable.setBounds(
                0,
                0,
                0 + result!!.intrinsicWidth,
                0 + result.intrinsicHeight
            )
            urlDrawable.drawable = result
            container.invalidate()
            val t: CharSequence = container.text
            container.text = t
        }

        fun fetchDrawable(urlString: String): Drawable {
            val `is`: InputStream = fetch(urlString)
            val drawable = Drawable.createFromStream(`is`, "src")
            drawable.setBounds(
                0,
                0,
                0 + drawable.intrinsicWidth,
                0 + drawable.intrinsicHeight
            )
            return drawable
        }

        @Throws(MalformedURLException::class, IOException::class)
        private fun fetch(urlString: String): InputStream {
            val httpClient = Networking.okhttpClient
            val request = Request.Builder().url(urlString).build()
            val response: Response = httpClient.newCall(request).execute()
            return response.body!!.byteStream()
        }
    }
}