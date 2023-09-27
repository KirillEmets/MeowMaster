package com.kirillyemets.catapp.data

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class AuthInterceptor(private val apiKey: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val modifiedRequest = originalRequest
            .newBuilder()
            .let {
                when {
                    apiKey.isNotBlank() -> it.header("x-api-key", apiKey)
                    else -> it
                }
            }.build()


        return chain.proceed(modifiedRequest)
    }
}