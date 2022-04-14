package tv.olaris.android.service.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("has_error")
    val hasError: Boolean = false,
    val message: String = "",
    val jwt: String? = null,
)