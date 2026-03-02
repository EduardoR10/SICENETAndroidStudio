package com.example.sicenet.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.local.LocalDataSource
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.remote.CargaAcademicaParser

class SaveCargaWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Recibe el JSON del FetchCargaWorker
        val jsonString = inputData.getString("JSON_CARGA") ?: return Result.failure()

        return try {
            val db = SicenetDatabase.getDatabase(applicationContext)
            val localDataSource = LocalDataSource(db.cargaAcademicaDao())

            // Parsea y guarda en Base de Datos
            val entidades = CargaAcademicaParser.parse(jsonString)
            localDataSource.saveCargaAcademica(entidades)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}