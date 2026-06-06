package com.prolearn.codecraftfront.data.api

import android.os.Build
import android.util.Log
import com.prolearn.codecraftfront.BuildConfig

/**
 * URL бэкенда:
 * - Эмулятор: 10.0.2.2 (хост-машина)
 * - Телефон: задайте API_BASE_URL в local.properties (IPv4 ПК, та же Wi-Fi сеть)
 */
object ServerConfig {
    private const val TAG = "ServerConfig"
    private const val EMULATOR_URL = "http://10.0.2.2:8080/api/v1/"
    private const val FALLBACK_LAN_IP = "192.168.0.1" // замените через API_BASE_URL в local.properties

    private val isEmulator: Boolean
        get() = Build.FINGERPRINT.contains("generic", ignoreCase = true) ||
            Build.MODEL.contains("Emulator", ignoreCase = true) ||
            Build.MODEL.contains("Android SDK built for", ignoreCase = true) ||
            Build.MANUFACTURER.contains("Genymotion", ignoreCase = true) ||
            Build.PRODUCT.contains("sdk", ignoreCase = true) ||
            Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
            Build.HARDWARE.contains("ranchu", ignoreCase = true)

    private fun normalizeBaseUrl(url: String): String {
        val trimmed = url.trim()
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }

    val BASE_URL: String = run {
        if (isEmulator) {
            Log.i(TAG, "Emulator detected → $EMULATOR_URL")
            return@run EMULATOR_URL
        }
        val fromProps = BuildConfig.API_BASE_URL.trim()
        if (fromProps.isNotEmpty()) {
            val url = normalizeBaseUrl(fromProps)
            Log.i(TAG, "Physical device → $url (from local.properties)")
            return@run url
        }
        val fallback = "http://$FALLBACK_LAN_IP:8080/api/v1/"
        Log.w(
            TAG,
            "Physical device: API_BASE_URL не задан в local.properties. " +
                "Используется $fallback — укажите IPv4 ПК (ipconfig), тот же Wi-Fi что у телефона.",
        )
        fallback
    }
}
