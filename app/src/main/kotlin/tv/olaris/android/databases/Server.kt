package tv.olaris.android.databases

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "servers")
data class Server constructor(
    var url: String,
    var username: String,
    var password: String,
    var name: String,
    var currentJWT: String,
    var version: String,
    @ColumnInfo(defaultValue = "0")
    var isCurrent: Boolean,
    @ColumnInfo(defaultValue = "false")
    var isOnline: Boolean,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

@Dao
interface ServerDoa {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertServer(server: Server)

    @Query("select * from servers")
    fun getServers(): Flow<List<Server>>

    @Query("select * from servers")
    suspend fun getServersOnce(): List<Server>

    @get:Query("select count(*) from servers")
    val serverCount: Int

    @Query("select * from servers WHERE id = :id")
    suspend fun getServerById(id: Int): Server

    @Query("select * from servers WHERE isCurrent = 1")
    suspend fun getCurrentServer(): Server?

    @Update(entity = Server::class)
    suspend fun update(obj: Server)

    @Delete
    suspend fun delete(model: Server)
}

@Database(entities = [Server::class], version = 5, exportSchema = false)
abstract class ServerDatabase : RoomDatabase() {
    abstract fun serverDoa(): ServerDoa
}
