package com.example.simplechat.core.platform

import com.example.simplechat.data.storage.SecureStorage

expect fun createSecureStorage(configuration: PlatformConfiguration): SecureStorage
