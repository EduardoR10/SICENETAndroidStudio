package com.example.sicenet.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenet.data.SicenetRepository

class FetchCalifFinalesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = SicenetRepository()
        // Según tu SOAP, enviamos "1" por defecto para modalidad educativa presencial
        val result = repository.getCalifFinalesRemotas(1)

        return if (result.isSuccess) {
            val json = result.getOrDefault("[]")
            val output = Data.Builder().putString("JSON_CALIF_FINALES", json).build()
            Result.success(output)
        } else {
            Result.failure()
        }
    }
}