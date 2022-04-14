package tv.olaris.android.ui.addServer

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tv.olaris.android.OlarisApplication
import tv.olaris.android.R

class ServerViewModel: ViewModel() {
      var serverUrl = MutableLiveData<String>()
      var username = MutableLiveData<String>()
      var password = MutableLiveData<String>()
      
      data class ValidationResult(var valid: Boolean = true, var errorText: String = "")

      fun validateUrl(url: String?) : ValidationResult {
            val result = ValidationResult()

            if (url != null && url.toString() != "") {
                  if (!URLUtil.isValidUrl(url.toString())) {
                        result.errorText = OlarisApplication.applicationContext().getString(R.string.error_invalid_url)
                        result.valid = false
                  }

            } else {
                  result.errorText = OlarisApplication.applicationContext().getString(R.string.error_invalid_url_format)
                  result.valid = false
            }

            return result
      }
}