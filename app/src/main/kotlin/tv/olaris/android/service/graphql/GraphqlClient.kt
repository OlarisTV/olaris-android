package tv.olaris.android.service.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient

fun graphqlClient(okHttpClient: OkHttpClient) = ApolloClient.Builder()
    .okHttpClient(okHttpClient)
    .serverUrl("")
    .build()
