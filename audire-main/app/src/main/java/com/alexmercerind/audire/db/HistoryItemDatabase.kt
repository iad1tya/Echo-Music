package com.alexmercerind.audire.db

import android.app.Application
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexmercerind.audire.models.HistoryItem

@Database(
    entities = [HistoryItem::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class HistoryItemDatabase : RoomDatabase() {
    abstract fun historyItemDao(): HistoryItemDao

    companion object {
        @Volatile
        private var instance: HistoryItemDatabase? = null
        private val lock = Any()
        operator fun invoke(application: Application) = instance ?: synchronized(lock) {
            instance ?: createDatabase(application).also { instance = it }
        }

        private fun createDatabase(application: Application) =
            Room.databaseBuilder(
                application,
                HistoryItemDatabase::class.java,
                "history-item-database"
            ).build()
    }
}
