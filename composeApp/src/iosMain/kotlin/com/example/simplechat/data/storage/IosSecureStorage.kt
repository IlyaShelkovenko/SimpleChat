package com.example.simplechat.data.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

class IosSecureStorage : SecureStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun read(key: String): String? = withContext(Dispatchers.Default) {
        defaults.stringForKey(key)
    }

    override suspend fun write(key: String, value: String) = withContext(Dispatchers.Default) {
        defaults.setObject(value, key)
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.Default) {
        defaults.removeObjectForKey(key)
    }
}
