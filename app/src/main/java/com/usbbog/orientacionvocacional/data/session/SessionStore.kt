package com.usbbog.orientacionvocacional.data.session

import android.content.Context
import android.content.SharedPreferences

data class SessionSnapshot(
    val token: String,
    val username: String,
    val role: String,
)

/**
 * Mantiene el JWT solo durante el proceso salvo que el usuario active
 * "Recordarme". En ese caso se conserva en almacenamiento privado de la app.
 */
object SessionStore {

    private const val PREFERENCES_NAME = "vocational_session"
    private const val KEY_TOKEN = "token"
    private const val KEY_USERNAME = "username"
    private const val KEY_ROLE = "role"

    @Volatile
    private var preferences: SharedPreferences? = null

    @Volatile
    private var inMemorySession: SessionSnapshot? = null

    fun initialize(context: Context) {
        if (preferences != null) return

        synchronized(this) {
            if (preferences != null) return
            val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            preferences = prefs

            val token = prefs.getString(KEY_TOKEN, null).orEmpty()
            if (token.isNotBlank()) {
                inMemorySession = SessionSnapshot(
                    token = token,
                    username = prefs.getString(KEY_USERNAME, "").orEmpty(),
                    role = prefs.getString(KEY_ROLE, "").orEmpty(),
                )
            }
        }
    }

    val session: SessionSnapshot?
        get() = inMemorySession

    val token: String?
        get() = inMemorySession?.token

    fun save(
        token: String,
        username: String,
        role: String,
        remember: Boolean,
    ) {
        inMemorySession = SessionSnapshot(token, username, role)

        val editor = preferences?.edit() ?: return
        if (remember) {
            editor
                .putString(KEY_TOKEN, token)
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .apply()
        } else {
            editor.clear().apply()
        }
    }

    fun clear() {
        inMemorySession = null
        preferences?.edit()?.clear()?.apply()
    }
}
