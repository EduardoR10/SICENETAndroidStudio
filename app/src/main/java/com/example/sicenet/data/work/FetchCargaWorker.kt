package com.example.sicenet.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenet.data.remote.AcademicDataSource

class FetchCargaWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val dataSource = AcademicDataSource()
        val result = dataSource.getCargaAcademica()

        return if (result.isSuccess) {
            val json = result.getOrDefault("[]")
            // Pasamos el JSON como dato de salida para el siguiente Worker
            val outputData = Data.Builder().putString("JSON_CARGA", json).build()
            Result.success(outputData)
        } else {
            Result.failure()
        }
    }
}