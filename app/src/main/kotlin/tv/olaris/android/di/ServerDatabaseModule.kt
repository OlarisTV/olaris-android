package tv.olaris.android.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tv.olaris.android.databases.ServerDatabase
import tv.olaris.android.databases.ServerDoa
import tv.olaris.android.repositories.ServersRepository

val serverDatabaseModule = module {

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE servers ADD COLUMN isCurrent INTEGER DEFAULT 0 NOT NULL")
        }
    }

    fun getDatabase(context: Context): ServerDatabase {
        return Room.databaseBuilder(
            context,
            ServerDatabase::class.java,
            "servers_db"
        )
            .addMigrations(MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideDao(database: ServerDatabase): ServerDoa {
        return database.serverDoa()
    }

    singleOf(::getDatabase)
    singleOf(::provideDao)
    singleOf(::ServersRepository)
}
