package com.alexmercerind.audire.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexmercerind.audire.models.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyItem: HistoryItem)

    @Delete
    suspend fun delete(historyItem: HistoryItem)

    @Query("SELECT * FROM history_item ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history_item WHERE LOWER(title) LIKE '%' || :term || '%' ORDER BY timestamp DESC")
    suspend fun search(term: String): List<HistoryItem>

    @Query("UPDATE history_item SET liked = 1 WHERE id = :id")
    suspend fun like(id: Int)

    @Query("UPDATE history_item SET liked = 0 WHERE id = :id")
    suspend fun unlike(id: Int)
}
