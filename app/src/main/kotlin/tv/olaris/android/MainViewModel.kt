package tv.olaris.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import tv.olaris.android.databases.Server
import tv.olaris.android.repositories.ServersRepository
import tv.olaris.android.service.http.OlarisHttpService
import java.net.ConnectException
import kotlin.time.Duration

class MainViewModel(
    private val serversRepository: ServersRepository,
    private val olarisHttpService: OlarisHttpService,
) : ViewModel() {

    private suspend fun checkServerStatus() {
        for (server in serversRepository.servers()) {
            checkServer(server)
        }
    }

    suspend fun newCheckServer(server: Server): Boolean {
        return try {
            olarisHttpService.getVersion(server.url)
            true
        } catch (e: ConnectException) {
            false
        }
    }

    private suspend fun checkServer(server: Server, updateRecord: Boolean = true): Boolean {
        Log.d("refreshDebug", "Checking if server: ${server.name}, is online")

        try {
            val version = olarisHttpService.getVersion(server.url)

            if (version != server.version || !server.isOnline) {
                server.version = version
                if (!server.isOnline) {
                    server.isOnline = true
                }
                Log.d("refreshDebug", "Server is ${server.isOnline}")

                if (updateRecord) {
                    Log.d("refreshDebug", "Updating record")

                    serversRepository.updateServer(server)
                }
            } else {
                Log.d("refreshDebug", "No change for ${server.name}.")
            }
            return true

        } catch (e: ConnectException) {
            setOffline(server, updateRecord)
            return false
        } catch (exception: java.net.SocketTimeoutException) {
            setOffline(server, updateRecord)
            return false
        }
    }

    suspend fun setOffline(server: Server, updateRecord: Boolean) {
        if (server.isOnline) {
            server.isOnline = false
            Log.d("refreshDebug", "Server is ${server.isOnline}")

            if (updateRecord) {
                Log.d("refreshDebug", "Updating record")
                serversRepository.updateServer(server)
            }
        }
    }

    suspend fun deleteServer(server: Server) = serversRepository.deleteServer(server)

    fun allServers() = serversRepository.allServers()

    suspend fun initTimer() = flow {
        delay(Duration.ZERO)
        while (true) {
            emit(checkServerStatus())
            delay(2000)
        }
    }

    fun setCurrentServer(server: Server) = viewModelScope.launch {
        serversRepository.setCurrentServer(server)
    }
}
