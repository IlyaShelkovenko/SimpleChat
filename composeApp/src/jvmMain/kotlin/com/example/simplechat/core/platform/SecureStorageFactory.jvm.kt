package com.example.simplechat.core.platform

import com.example.simplechat.data.storage.JvmSecureStorage
import com.example.simplechat.data.storage.SecureStorage

actual fun createSecureStorage(configuration: PlatformConfiguration): SecureStorage {
    return JvmSecureStorage()
}
