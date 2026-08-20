package com.usbbog.orientacionvocacional.platform

import com.usbbog.orientacionvocacional.data.session.SessionStorage
import platform.Foundation.NSUserDefaults

internal class IosSessionStorage : SessionStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun read(key: String): String? = defaults.stringForKey(key)

    override fun write(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    override fun clear() {
        listOf("token", "username", "role").forEach { key ->
            defaults.removeObjectForKey(key)
        }
    }
}
