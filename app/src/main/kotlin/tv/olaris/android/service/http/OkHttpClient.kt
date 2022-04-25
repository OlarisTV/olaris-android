package tv.olaris.android.service.http

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(2, TimeUnit.SECONDS)
    .writeTimeout(3, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .addNetworkInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .addInterceptor { chain: Interceptor.Chain ->
        val original: Request = chain.request()
        val builder: Request.Builder =
            original.newBuilder().method(original.method, original.body)
        chain.proceed(builder.build())
    }
    .build()
