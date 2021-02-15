package tv.olaris.android.repositories

import android.util.Log
import kotlinx.coroutines.flow.Flow
import tv.olaris.android.databases.Server
import tv.olaris.android.databases.ServerDoa
import tv.olaris.android.service.http.OlarisHttpService
import tv.olaris.android.service.http.model.LoginResponse

class ServersRepository(private val serverDoa: ServerDoa) {
    val allServers: Flow<List<Server>> = serverDoa.getServers()


    fun servers(): List<Server> {
     return serverDoa.getServersOnce()
    }

    suspend fun insertServer(server: Server) {
        serverDoa.insertServer(server)
    }

    suspend fun getServerById(id: Int): Server {
        return serverDoa.getServerById(id)
    }




    suspend fun updateServer(server: Server) {
        serverDoa.update(server)
    }

    fun getServerCount(): Int {
        return serverDoa.serverCount
    }

    suspend fun deleteServer(server: Server) {
        serverDoa.delete(server)
    }
}