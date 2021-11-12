package com.example.habrrss

import okhttp3.OkHttpClient

object Networking {
    val okhttpClient = OkHttpClient.Builder().build()
    val parser = HabrXmlParser()
}