package tv.olaris.android

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tv.olaris.android.databases.Server
import tv.olaris.android.databases.ServerDatabase
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository
import tv.olaris.android.service.graphql.GraphqlClient
import tv.olaris.android.service.graphql.GrapqhClientManager
import tv.olaris.android.service.http.OlarisHttpService
import java.net.ConnectException
import java.util.*

@HiltAndroidApp
class OlarisApplication : Application() {
    var initialNavigation: Boolean = false

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { ServerDatabase.getDatabase(this, applicationScope) }

    // For the GraphQLClient I rewrote this to a manager class, should we do that here as well?
    private val _graphqlClients: MutableMap<Int, OlarisGraphQLRepository> = mutableMapOf()

    private val clientManager = GrapqhClientManager()

    // TODO: Is this an ok way of doing this?
    val serversRepository by lazy { ServersRepository(database.serverDoa()) }

    fun getClient(server: Server): GraphqlClient {
        return clientManager.createOrInit(server)
    }

    suspend fun getOrInitRepo(serverId: Int): OlarisGraphQLRepository {
        val TAG = "mediaplayer"
        if (!_graphqlClients.containsKey(serverId)) {
            Log.d(TAG, "getOrInitRepo: $serverId")

            _graphqlClients[serverId] =
                OlarisGraphQLRepository(serversRepository.getServerById(serverId))
        }

        return _graphqlClients.getValue(serverId)
    }

    suspend fun checkServerStatus() {
        applicationScope.launch {
            for (server in serversRepository.servers()) {
                checkServer(server)
            }
        }
    }

    suspend fun newCheckServer(server: Server): Boolean {
        return try {
            OlarisHttpService(server.url).getVersion()
            true
        } catch (e: ConnectException) {
            false
        }
    }

    suspend fun checkServer(server: Server, updateRecord: Boolean = true): Boolean {
        Log.d("refreshDebug", "Checking if server: ${server.name}, is online")

        try {
            val version = OlarisHttpService(server.url).getVersion()

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
            }else {
                Log.d("refreshDebug", "No change for ${server.name}.")
            }
            return true

        } catch (e: ConnectException) {
            setOffline(server, updateRecord)
            return false
        } catch(exception: java.net.SocketTimeoutException){
            setOffline(server, updateRecord)
            return false
        }
    }

    suspend fun setOffline(server: Server, updateRecord: Boolean){
        if (server.isOnline) {
            server.isOnline = false
            Log.d("refreshDebug", "Server is ${server.isOnline}")

            if (updateRecord) {
                Log.d("refreshDebug", "Updating record")
                serversRepository.updateServer(server)
            }
        }
    }

    init {
        instance = this

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                applicationScope.launch {
                    checkServerStatus()
                }
            }
        }, 2000, 12000)
    }

    companion object {
        private var instance: OlarisApplication? = null

        fun applicationContext(): OlarisApplication {
            return instance as OlarisApplication
        }
    }
}