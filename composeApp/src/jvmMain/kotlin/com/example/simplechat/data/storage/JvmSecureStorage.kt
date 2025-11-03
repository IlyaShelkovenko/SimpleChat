package com.example.simplechat.data.storage

import java.util.prefs.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JvmSecureStorage : SecureStorage {
    private val preferences = Preferences.userRoot().node("simple_chat")

    override suspend fun read(key: String): String? = withContext(Dispatchers.Default) {
        preferences.get(key, null)
    }

    override suspend fun write(key: String, value: String) = withContext(Dispatchers.Default) {
        preferences.put(key, value)
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.Default) {
        preferences.remove(key)
    }
}
