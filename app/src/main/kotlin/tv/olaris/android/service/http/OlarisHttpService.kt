package tv.olaris.android.service.http

import tv.olaris.android.service.http.model.LoginResponse

interface OlarisHttpService {

    suspend fun getVersion(baseUrl: String): String
    suspend fun loginUser(baseUrl: String, username: String, password: String): LoginResponse
}
