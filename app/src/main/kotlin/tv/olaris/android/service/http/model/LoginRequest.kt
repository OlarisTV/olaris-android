package tv.olaris.android.service.http.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)
