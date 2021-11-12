package com.example.habrrss

import okhttp3.OkHttpClient
import org.xmlpull.v1.XmlPullParser

object Networking {
    val okhttpClient = OkHttpClient.Builder().build()
    val parser = HabrXmlParser()
}