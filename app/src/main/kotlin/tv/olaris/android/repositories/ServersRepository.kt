package tv.olaris.android.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import tv.olaris.android.databases.Server
import tv.olaris.android.databases.ServerDoa

class ServersRepository(private val serverDoa: ServerDoa) {

    fun allServers(): Flow<List<Server>> = serverDoa.getServers()

    suspend fun servers(): List<Server> = serverDoa.getServersOnce()

    suspend fun insertServer(server: Server) = serverDoa.insertServer(server)

    suspend fun getServerById(id: Int): Server = serverDoa.getServerById(id)

    suspend fun getCurrentServer(): Server = serverDoa.getCurrentServer() ?: throw NullPointerException()

    suspend fun updateServer(server: Server) = serverDoa.update(server)

    /* TODO: Is there a better way to handle this?
        If current server is set asynchronously it likely won't be set before network calls are made
    */
    fun setCurrentServer(server: Server) = runBlocking {
        with(serverDoa) {
            getCurrentServer()?.let {
                update(it.copy(isCurrent = false))
            }
            update(server.copy(isCurrent = true))
        }
    }

    fun getServerCount(): Int = serverDoa.serverCount

    suspend fun deleteServer(server: Server) = serverDoa.delete(server)
}
