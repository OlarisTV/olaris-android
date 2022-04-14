package tv.olaris.android.service.http

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import tv.olaris.android.service.http.model.LoginRequest
import tv.olaris.android.service.http.model.LoginResponse
import java.net.ConnectException

class OlarisHttpService(private val baseUrl: String) {

    suspend fun getVersion() : String {
        val versionURL = "$baseUrl/olaris/m/v1/version"
        return try {
            val client = HttpClient(Android) {
                install(HttpTimeout){
                    requestTimeoutMillis = 5000
                }
            }
            client.get(versionURL).body()
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 404) {
                ""
            } else {
                Log.e("olarisHttpServer", "Received an error: ${e.message}")
                "TODO"
            }
        }
    }

    suspend fun loginUser(username: String, password: String) : LoginResponse {
        val authLoginUrl = "$baseUrl/olaris/m/v1/auth"
        Log.d("olarisHttpServer", "Login URL: $authLoginUrl")
        try{
            val client = HttpClient(Android) {
                install(Resources)
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
                expectSuccess = false
            }
            val loginResponse: LoginResponse = client.post(authLoginUrl) {
                setBody(LoginRequest(username, password))
                contentType(ContentType.Application.Json)
            }.body()
            Log.d("http", "Login response $loginResponse")
            return loginResponse
        }catch(e: ClientRequestException){
            Log.d("http", "Error: ${e.message}")
            return LoginResponse(true, e.toString())
        }catch(e: ConnectException){
            return LoginResponse(true, e.toString())
        }catch(e: NoTransformationFoundException){
            return LoginResponse(true, "Server did not respond with JSON, are you sure this is the correct URL?")
        }
    }
}