package tv.olaris.android.ui.addServer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication
import tv.olaris.android.R
import tv.olaris.android.databases.Server
import tv.olaris.android.databinding.FragmentAddServerBinding
import tv.olaris.android.service.http.OlarisHttpService
import tv.olaris.android.util.hideKeyboard

class AddServerFragment : Fragment() {
    private var _binding: FragmentAddServerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ServerViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddServer.setOnClickListener{
            var hasErrors = false
            hideKeyboard()

            with(viewModel.validateUrl(binding.textEditServerUrl.text.toString())){
                if(!this.valid){
                    binding.textEditServerUrl.error = this.errorText
                }
            }

            with(binding.textEditUsername){
                if(this.text.toString() == ""){
                    this.error = context.getString(R.string.error_no_username)
                    hasErrors = true
                }
            }

            with(binding.textEditPassword){
                if(this.text.toString() == ""){
                    this.error = context.getString(R.string.error_no_password)
                    hasErrors = true
                }
            }
            with(binding.textEditServerName){
                if(this.text.toString() == ""){
                    this.error = context.getString(R.string.error_no_server_label)
                    hasErrors = true
                }
            }

            Log.d("http", "Starting login: $hasErrors")

            if(!hasErrors){
                Log.d("http", "No errors!")

                lifecycle.coroutineScope.launch{
                    val service = OlarisHttpService(binding.textEditServerUrl.text.toString().removeSuffix("/"))
                    val loginResponse = service.loginUser(
                        binding.textEditUsername.text.toString(),
                        binding.textEditPassword.text.toString())

                    if(loginResponse.hasError){
                        Log.d("http", "Got error ${loginResponse.message}")
                        Snackbar.make(view, "Error: ${loginResponse.message}", Snackbar.LENGTH_LONG).show()
                    }else {
                        Log.d("http", "Everything ok! ${loginResponse.jwt}")
                        Toast.makeText(view.context, "Successfully added server.", Toast.LENGTH_LONG).show()
                        val version = service.getVersion()
                        OlarisApplication.applicationContext().serversRepository.insertServer(
                            Server(
                                binding.textEditServerUrl.text.toString(),
                                binding.textEditUsername.text.toString(),
                                binding.textEditPassword.text.toString(),
                                binding.textEditServerName.text.toString(),
                                loginResponse.jwt.toString(),
                                version,
                                isOnline = true
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddServerBinding.inflate(inflater, container, false)

        return binding.root
    }
}
