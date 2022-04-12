package tv.olaris.android.service.graphql

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.auth0.android.jwt.JWT
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import tv.olaris.android.databases.Server
import tv.olaris.android.service.http.OlarisHttpService
import tv.olaris.android.service.http.model.LoginResponse
import java.util.concurrent.TimeUnit


class GrapqhClientManager {
    private val clients: MutableMap<Int, GraphqlClient> = mutableMapOf()

    fun createOrInit(server: Server): GraphqlClient {
        if (!clients.containsKey(server.id)) {
            clients[server.id] = GraphqlClient(server)
        }
        return clients[server.id]!!
    }
}

class GraphqlClient(var server: Server) {
    private var jwt: String? = null

    private suspend fun refreshJwt() {
        val result: LoginResponse =
            OlarisHttpService(server.url).loginUser(server.username, server.password)
        if (!result.hasError && result.jwt != null) {
            Log.d("refreshDebug", "Updating JWT: ${result.jwt}")
            jwt = result.jwt

        }
    }

    suspend fun get(): ApolloClient {
        if (jwt == null || JWT(jwt!!).isExpired(10)) {
            refreshJwt()
        }

        var interceptor = HttpLoggingInterceptor()

        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .addInterceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val builder: Request.Builder =
                    original.newBuilder().method(original.method, original.body)
                builder.header("Authorization", "Bearer ${jwt}")
                chain.proceed(builder.build())
            }
            .build()

        return ApolloClient.builder()
            .serverUrl("${server.url}/olaris/m/query")
            .okHttpClient(okHttpClient)
            .build()
    }

}