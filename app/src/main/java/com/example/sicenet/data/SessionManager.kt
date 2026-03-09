package com.example.sicenet.data

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "sicenet_prefs"
    private var prefs: SharedPreferences? = null

    // Inicializamos con el contexto de la app
    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    // Cookie temporal en memoria (solo dura mientras la app está abierta)
    var sessionCookie: String? = null

    fun isLoggedIn() = sessionCookie != null

    // Guardar sesión permanentemente
    fun saveCredentials(matricula: String, pass: String) {
        prefs?.edit()?.apply {
            putString("MATRICULA", matricula)
            putString("PASSWORD", pass)
            putBoolean("HAS_SESSION", true)
            apply()
        }
    }

    // Borrar sesión permanentemente
    fun clearCredentials() {
        prefs?.edit()?.apply {
            remove("MATRICULA")
            remove("PASSWORD")
            putBoolean("HAS_SESSION", false)
            apply()
        }
    }

    fun hasLastSession(): Boolean = prefs?.getBoolean("HAS_SESSION", false) ?: false
    fun getLastMatricula(): String = prefs?.getString("MATRICULA", "") ?: ""
    fun getLastPassword(): String = prefs?.getString("PASSWORD", "") ?: ""
}