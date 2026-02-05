package com.example.sicenet.data

object SessionManager {
    var sessionCookie: String? = null

    fun isLoggedIn(): Boolean {
        return !sessionCookie.isNullOrEmpty()
    }

    fun clearSession() {
        sessionCookie = null
    }
}