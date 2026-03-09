package com.example.sicenet.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenet.data.SicenetRepository

class FetchCalifUnidadesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = SicenetRepository() // Pasa por el Repositorio
        val result = repository.getCalifUnidadesRemotas()

        return if (result.isSuccess) {
            val json = result.getOrDefault("[]")
            val output = Data.Builder().putString("JSON_CALIF_UNIDADES", json).build()
            Result.success(output)
        } else {
            Result.failure()
        }
    }
}