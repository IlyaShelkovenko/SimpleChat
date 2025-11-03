package com.example.simplechat.core.platform

import com.example.simplechat.data.storage.AndroidSecureStorage
import com.example.simplechat.data.storage.SecureStorage

actual fun createSecureStorage(configuration: PlatformConfiguration): SecureStorage {
    return AndroidSecureStorage(configuration.context)
}
