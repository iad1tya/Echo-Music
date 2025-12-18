package com.alexmercerind.audire.repository

import android.app.Application
import android.net.Uri
import com.alexmercerind.audire.models.HistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ImportExportRepository(private val application: Application) {
    suspend fun import(uri: Uri) {
        val input = application.contentResolver.openInputStream(uri)
        input?.use {
            val json = it.readBytes().toString(Charsets.UTF_16)
            val historyItems: List<HistoryItem> = gson.fromJson(
                json, object : TypeToken<List<HistoryItem>>() {}.type
            )
            for (historyItem in historyItems) {
                HistoryRepository(application).insert(historyItem)
            }
        }
    }

    suspend fun export(uri: Uri, historyItems: List<HistoryItem>) {
        val output = application.contentResolver.openOutputStream(uri)
        output?.use {
            val json = gson.toJson(historyItems)
            it.write(json.toByteArray(Charsets.UTF_16))
        }
    }

    companion object {
        private val gson by lazy { Gson() }
    }
}
