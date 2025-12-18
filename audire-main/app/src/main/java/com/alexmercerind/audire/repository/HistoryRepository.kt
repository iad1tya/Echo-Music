package com.alexmercerind.audire.repository

import android.app.Application
import com.alexmercerind.audire.db.HistoryItemDatabase
import com.alexmercerind.audire.models.HistoryItem

class HistoryRepository(private val application: Application) {
    suspend fun insert(historyItem: HistoryItem) =
        HistoryItemDatabase(application).historyItemDao().insert(historyItem)

    suspend fun delete(historyItem: HistoryItem) =
        HistoryItemDatabase(application).historyItemDao().delete(historyItem)

    fun getAll() = HistoryItemDatabase(application).historyItemDao().getAll()

    suspend fun search(term: String) =
        HistoryItemDatabase(application).historyItemDao().search(term)

    suspend fun like(historyItem: HistoryItem) =
        HistoryItemDatabase(application).historyItemDao().like(historyItem.id!!)

    suspend fun unlike(historyItem: HistoryItem) =
        HistoryItemDatabase(application).historyItemDao().unlike(historyItem.id!!)

}
