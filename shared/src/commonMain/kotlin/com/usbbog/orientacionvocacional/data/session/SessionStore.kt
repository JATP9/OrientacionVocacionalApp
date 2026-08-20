package com.usbbog.orientacionvocacional.data.session

data class SessionSnapshot(
    val token: String,
    val username: String,
    val role: String,
)

interface SessionStorage {
    fun read(key: String): String?
    fun write(key: String, value: String)
    fun clear()
}

/**
 * Mantiene el JWT solo durante el proceso salvo que el usuario active
 * “Recordarme”. En ese caso delega la persistencia al almacenamiento privado
 * provisto por Android o iOS.
 */
object SessionStore {
    private const val KEY_TOKEN = "token"
    private const val KEY_USERNAME = "username"
    private const val KEY_ROLE = "role"

    private var storage: SessionStorage? = null
    private var inMemorySession: SessionSnapshot? = null

    fun initialize(storage: SessionStorage) {
        if (this.storage != null) return
        this.storage = storage

        val token = storage.read(KEY_TOKEN).orEmpty()
        if (token.isNotBlank()) {
            inMemorySession = SessionSnapshot(
                token = token,
                username = storage.read(KEY_USERNAME).orEmpty(),
                role = storage.read(KEY_ROLE).orEmpty(),
            )
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

        val activeStorage = storage ?: return
        if (remember) {
            activeStorage.write(KEY_TOKEN, token)
            activeStorage.write(KEY_USERNAME, username)
            activeStorage.write(KEY_ROLE, role)
        } else {
            activeStorage.clear()
        }
    }

    fun clear() {
        inMemorySession = null
        storage?.clear()
    }
}
