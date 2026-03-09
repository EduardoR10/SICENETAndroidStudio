package com.example.sicenet.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenet.data.SicenetRepository
import java.io.File

class FetchKardexWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = SicenetRepository() // Pasa por el Repositorio
        val result = repository.getKardexRemoto(3)

        return if (result.isSuccess) {
            val json = result.getOrDefault("")
            val tempFile = File(applicationContext.cacheDir, "kardex_temp.json")
            tempFile.writeText(json)
            val output = Data.Builder().putString("KARDEX_FILE_PATH", tempFile.absolutePath).build()
            Result.success(output)
        } else {
            Result.failure()
        }
    }
}