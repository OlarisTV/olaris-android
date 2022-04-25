package tv.olaris.android.service.http

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import tv.olaris.android.databases.Server

class TokenRefreshAuthenticator(
    private val olarisHttpService: OlarisHttpService,
    private val server: Server,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? = when {
        response.retryCount > 2 -> null
        else -> response.createSignedRequest()
    }

    private fun Response.createSignedRequest(): Request? = try {
        val jwt = runBlocking {
            olarisHttpService.loginUser(
                server.url,
                server.username,
                server.password
            )
        }.jwt
        request.signWithToken(jwt)
    } catch (error: Throwable) {
        Log.e("error", "Failed to re-sign request")
        null
    }

    private val Response.retryCount: Int
        get() {
            var currentResponse = priorResponse
            var result = 0
            while (currentResponse != null) {
                result++
                currentResponse = currentResponse.priorResponse
            }
            return result
        }
}

fun Request.signWithToken(jwt: String?) =
    newBuilder()
        .header("Authorization", "Bearer $jwt")
        .build()
