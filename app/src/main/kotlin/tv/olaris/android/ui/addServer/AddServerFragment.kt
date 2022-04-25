package tv.olaris.android.ui.addServer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.olaris.android.R
import tv.olaris.android.databases.Server
import tv.olaris.android.databinding.FragmentAddServerBinding
import tv.olaris.android.service.http.OlarisHttpService
import tv.olaris.android.ui.base.BaseFragment
import tv.olaris.android.util.hideKeyboard

class AddServerFragment :
    BaseFragment<FragmentAddServerBinding>(FragmentAddServerBinding::inflate) {
    private val viewModel: ServerViewModel by viewModel()
    private val olarisHttpService: OlarisHttpService by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddServer.setOnClickListener {
            var hasErrors = false
            hideKeyboard()

            with(viewModel.validateUrl(binding.textEditServerUrl.text.toString())) {
                if (!this.valid) {
                    binding.textEditServerUrl.error = this.errorText
                }
            }

            with(binding.textEditUsername) {
                if (this.text.toString() == "") {
                    this.error = context.getString(R.string.error_no_username)
                    hasErrors = true
                }
            }

            with(binding.textEditPassword) {
                if (this.text.toString() == "") {
                    this.error = context.getString(R.string.error_no_password)
                    hasErrors = true
                }
            }
            with(binding.textEditServerName) {
                if (this.text.toString() == "") {
                    this.error = context.getString(R.string.error_no_server_label)
                    hasErrors = true
                }
            }

            Log.d("http", "Starting login: $hasErrors")

            if (!hasErrors) {
                Log.d("http", "No errors!")

                lifecycle.coroutineScope.launch {
                    val loginResponse = olarisHttpService.loginUser(
                        binding.textEditServerUrl.text.toString().removeSuffix("/"),
                        binding.textEditUsername.text.toString(),
                        binding.textEditPassword.text.toString()
                    )

                    if (loginResponse.hasError) {
                        Log.d("http", "Got error ${loginResponse.message}")
                        Snackbar.make(view, "Error: ${loginResponse.message}", Snackbar.LENGTH_LONG)
                            .show()
                    } else {
                        Log.d("http", "Everything ok! ${loginResponse.jwt}")
                        Toast.makeText(
                            view.context,
                            "Successfully added server.",
                            Toast.LENGTH_LONG
                        ).show()
                        val serverUrl = binding.textEditServerUrl.text.toString()
                        val version = olarisHttpService.getVersion(serverUrl)

                        viewModel.insertServer(
                            Server(
                                serverUrl,
                                binding.textEditUsername.text.toString(),
                                binding.textEditPassword.text.toString(),
                                binding.textEditServerName.text.toString(),
                                loginResponse.jwt.toString(),
                                version,
                                isOnline = true,
                                isCurrent = true,
                            )
                        )
                        val action =
                            AddServerFragmentDirections.actionFragmentAddServerToFragmentServerList()
                        view.findNavController().navigate(action)
                    }
                }
            }
            Log.d("http", "Done doing login")
        }
    }
}
