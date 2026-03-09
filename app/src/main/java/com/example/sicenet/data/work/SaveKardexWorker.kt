package com.example.sicenet.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.remote.KardexParser
import java.io.File

class SaveKardexWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Recibimos la ruta del archivo del Worker anterior
        val filePath = inputData.getString("KARDEX_FILE_PATH") ?: return Result.failure()

        return try {
            val file = File(filePath)
            if (!file.exists()) return Result.failure()

            // Leemos el JSON inmenso desde el archivo
            val jsonString = file.readText()

            val db = SicenetDatabase.getDatabase(applicationContext)
            val entidades = KardexParser.parse(jsonString)
            db.kardexDao().updateKardex(entidades)

            // Limpiamos el archivo temporal para no ocupar espacio basura
            file.delete()

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}