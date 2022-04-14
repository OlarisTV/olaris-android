package tv.olaris.android.ui.serverList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentServerListBinding
import tv.olaris.android.ui.base.BaseFragment

class ServerListFragment : BaseFragment<FragmentServerListBinding>(FragmentServerListBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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