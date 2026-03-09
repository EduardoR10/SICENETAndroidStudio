package com.example.sicenet.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.remote.CalifUnidadParser

class SaveCalifUnidadesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val jsonString = inputData.getString("JSON_CALIF_UNIDADES") ?: return Result.failure()
        return try {
            val db = SicenetDatabase.getDatabase(applicationContext)
            val entidades = CalifUnidadParser.parse(jsonString)
            db.califUnidadDao().updateCalifUnidades(entidades)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}