package com.example.simplechat.data.storage

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Security.SecCopyErrorMessageString
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus

private const val SERVICE_NAME = "com.example.simplechat.securestorage"

class IosSecureStorage : SecureStorage {
    override suspend fun read(key: String): String? = withContext(Dispatchers.Default) {
        memScoped {
            val query = baseQuery(key).apply {
                setObject(kSecMatchLimitOne, forKey = kSecMatchLimit)
                setObject(kCFBooleanTrue, forKey = kSecReturnData)
            }

            val resultVar = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query as CFDictionaryRef, resultVar.ptr)
            when (status) {
                errSecSuccess -> {
                    val cfType: CFTypeRef? = resultVar.value
                    val data = cfType?.reinterpret<NSData>()
                    data?.stringValue()
                }
                errSecItemNotFound -> null
                else -> throw keychainError("read", status)
            }
        }
    }

    override suspend fun write(key: String, value: String) = withContext(Dispatchers.Default) {
        val data = value.toNSData()
        memScoped {
            val query = baseQuery(key)
            val attributes = NSMutableDictionary().apply {
                setObject(data, forKey = kSecValueData)
            }

            when (val status = SecItemUpdate(query as CFDictionaryRef, attributes as CFDictionaryRef)) {
                errSecSuccess -> Unit
                errSecItemNotFound -> addItem(key, data)
                else -> throw keychainError("update", status)
            }
        }
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.Default) {
        memScoped {
            val query = baseQuery(key)
            when (val status = SecItemDelete(query as CFDictionaryRef)) {
                errSecSuccess, errSecItemNotFound -> Unit
                else -> throw keychainError("remove", status)
            }
        }
    }

    private fun baseQuery(key: String) = NSMutableDictionary().apply {
        setObject(kSecClassGenericPassword, forKey = kSecClass)
        setObject(SERVICE_NAME, forKey = kSecAttrService)
        setObject(key, forKey = kSecAttrAccount)
        setObject(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly, forKey = kSecAttrAccessible)
    }

    private fun addItem(key: String, data: NSData) {
        val query = NSMutableDictionary().apply {
            setObject(kSecClassGenericPassword, forKey = kSecClass)
            setObject(SERVICE_NAME, forKey = kSecAttrService)
            setObject(key, forKey = kSecAttrAccount)
            setObject(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly, forKey = kSecAttrAccessible)
            setObject(data, forKey = kSecValueData)
        }

        val status = SecItemAdd(query as CFDictionaryRef, null)
        if (status != errSecSuccess) {
            throw keychainError("add", status)
        }
    }

    private fun keychainError(operation: String, status: OSStatus): IllegalStateException {
        val message = SecCopyErrorMessageString(status, null)?.toString()
        val description = message ?: "Keychain $operation failed with status $status"
        return IllegalStateException(description)
    }
}

private fun NSData.stringValue(): String? {
    val nsString = NSString.create(data = this, encoding = NSUTF8StringEncoding)
    return nsString?.toString()
}

private fun String.toNSData(): NSData {
    val byteArray = encodeToByteArray()
    return byteArray.usePinned {
        NSData.create(bytes = it.addressOf(0), length = byteArray.size.convert())
    }
}
