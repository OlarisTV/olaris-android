package tv.olaris.android.service.http

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import tv.olaris.android.service.http.model.LoginRequest
import tv.olaris.android.service.http.model.LoginResponse
import java.net.ConnectException

class OlarisHttpServiceImpl(
    private val client: HttpClient,
) : OlarisHttpService {

    override suspend fun getVersion(baseUrl: String): String {
        val versionURL = "$baseUrl/olaris/m/v1/version"
        return try {
            client.get(versionURL).body()
        } catch (e: HttpRequestTimeoutException) {
            Log.e("olarisHttpServer", "Received an error: ${e.message}")
            "Request timed out"
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 404) {
                ""
            } else {
                Log.e("olarisHttpServer", "Received an error: ${e.message}")
                "TODO"
            }
        }
    }

    override suspend fun loginUser(
        baseUrl: String,
        username: String,
        password: String,
    ): LoginResponse {
        val authLoginUrl = "$baseUrl/olaris/m/v1/auth"
        Log.d("olarisHttpServer", "Login URL: $authLoginUrl")
        try {
            val loginResponse: LoginResponse = client.post {
                url(authLoginUrl)
                setBody(LoginRequest(username, password))
                contentType(ContentType.Application.Json)
            }.body()

            Log.d("http", "Login response $loginResponse")
            return loginResponse
        } catch (e: ClientRequestException) {
            Log.d("http", "Error: ${e.message}")
            return LoginResponse(true, e.toString())
        } catch (e: ConnectException) {
            return LoginResponse(true, e.toString())
        } catch (e: NoTransformationFoundException) {
            return LoginResponse(
                true,
                "Server did not respond with JSON, are you sure this is the correct URL?"
            )
        }
    }
}
