package com.usbbog.orientacionvocacional

import android.content.Context
import com.usbbog.orientacionvocacional.data.session.SessionStorage

class AndroidSessionStorage(context: Context) : SessionStorage {
    private val preferences = context.applicationContext.getSharedPreferences(
        "vocational_session",
        Context.MODE_PRIVATE,
    )

    override fun read(key: String): String? = preferences.getString(key, null)

    override fun write(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override fun clear() {
        preferences.edit().clear().apply()
    }
}
