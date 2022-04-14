package tv.olaris.android.ui.serverList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentServerListBinding

class ServerListFragment : Fragment() {
    private var _binding: FragmentServerListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentServerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddServer.setOnClickListener{
            val action = ServerListFragmentDirections.actionFragmentServerListToFragmentAddServer()
            findNavController().navigate(action)
        }

        val adapter = ServerItemAdapter(this.requireContext())
        binding.recyclerServerList.adapter = adapter
        binding.recyclerServerList.layoutManager = LinearLayoutManager(this.requireContext())

        lifecycleScope.launch{
            OlarisApplication.applicationContext().serversRepository.allServers.collect {
                Log.d("Servers", it.toString())
                adapter.submitList(it)
                if(it.isNotEmpty()){
                    view.findViewById<TextView>(R.id.text_help_add_server).visibility = View.INVISIBLE
                    view.findViewById<TextView>(R.id.text_help_explanation_servers).visibility = View.INVISIBLE
                }else{
                    view.findViewById<TextView>(R.id.text_help_add_server).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.text_help_explanation_servers).visibility = View.VISIBLE
                }
            }
        }
    }
}