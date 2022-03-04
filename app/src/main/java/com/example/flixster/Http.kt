package com.example.flixster

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class Http {
    companion object {
        @JvmStatic
        fun createCall(url:String):Call{
            val client = OkHttpClient.Builder()
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .hostnameVerifier { _, _ -> true }.build()

            val request = Request.Builder()
                .url(url)
                .addHeader("user-agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                .addHeader("content-type","application/json")
                .get()
                .build()

            val call = client.newCall(request)
            return call
        }
    }
}