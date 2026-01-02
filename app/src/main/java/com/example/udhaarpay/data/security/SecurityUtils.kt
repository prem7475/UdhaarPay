package com.example.udhaarpay.data.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import timber.log.Timber
import java.io.File
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityUtils @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val TAG = "SecurityUtils"
        private val ROOT_INDICATORS = arrayOf(
            "/system/app/Superuser.apk",
            "/system/xbin/su",
            "/system/bin/su",
            "/system/xbin/daemonsu",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
    }

    /**
     * Check if device is rooted
     */
    fun isDeviceRooted(): Boolean {
        return try {
            // Check for root management apps
            val rootApps = arrayOf(
                "com.noshufou.android.su",
                "com.noshufou.android.su.elite",
                "eu.chainfire.supersu",
                "com.koushikdutta.superuser",
                "com.thirdparty.superuser",
                "com.yellowes.su"
            )

            val packageManager = context.packageManager
            for (app in rootApps) {
                try {
                    packageManager.getPackageInfo(app, PackageManager.GET_ACTIVITIES)
                    Timber.w(TAG, "Root management app detected: $app")
                    return true
                } catch (e: PackageManager.NameNotFoundException) {
                    // App not found, continue checking
                }
            }

            // Check for root files
            for (path in ROOT_INDICATORS) {
                if (File(path).exists()) {
                    Timber.w(TAG, "Root indicator found: $path")
                    return true
                }
            }

            // Check for busybox
            val busyboxPaths = arrayOf(
                "/system/xbin/busybox",
                "/system/bin/busybox",
                "/system/xbin/busybox"
            )

            for (path in busyboxPaths) {
                if (File(path).exists()) {
                    Timber.w(TAG, "BusyBox detected: $path")
                    return true
                }
            }

            false
        } catch (e: Exception) {
            Timber.e(TAG, "Error checking root status", e)
            false
        }
    }

    /**
     * Check if app is running on emulator
     */
    fun isEmulator(): Boolean {
        return try {
            val buildDetails = Build.FINGERPRINT + Build.DEVICE + Build.MODEL + Build.BRAND + Build.PRODUCT

            val emulatorIndicators = arrayOf(
                "generic",
                "unknown",
                "emulator",
                "sdk",
                "sdk_x86",
                "vbox86p",
                "genymotion"
            )

            emulatorIndicators.any { indicator ->
                buildDetails.contains(indicator, ignoreCase = true)
            }
        } catch (e: Exception) {
            Timber.e(TAG, "Error checking emulator status", e)
            false
        }
    }

    /**
     * Check if app is debuggable
     */
    fun isAppDebuggable(): Boolean {
        return try {
            (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            Timber.e(TAG, "Error checking debuggable status", e)
            false
        }
    }

    /**
     * Get app signature for verification
     */
    fun getAppSignature(): String? {
        return try {
            val packageManager = context.packageManager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo?.signatures
            }

            if (!signatures.isNullOrEmpty()) {
                val cert = signatures[0].toByteArray()
                val md = java.security.MessageDigest.getInstance("SHA-256")
                val digest = md.digest(cert)
                digest.joinToString("") { String.format("%02x", it) }
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(TAG, "Error getting app signature", e)
            null
        }
    }

    /**
     * Check if device has screen lock enabled
     */
    fun isDeviceSecure(): Boolean {
        return try {
            val keyguardManager = context.getSystemService(android.app.KeyguardManager::class.java)
            keyguardManager?.isDeviceSecure == true
        } catch (e: Exception) {
            Timber.e(TAG, "Error checking device security", e)
            false
        }
    }

    /**
     * Get security status summary
     */
    fun getSecurityStatus(): SecurityStatus {
        return SecurityStatus(
            isRooted = isDeviceRooted(),
            isEmulator = isEmulator(),
            isDebuggable = isAppDebuggable(),
            isDeviceSecure = isDeviceSecure(),
            appSignature = getAppSignature()
        )
    }

    /**
     * Security status data class
     */
    data class SecurityStatus(
        val isRooted: Boolean,
        val isEmulator: Boolean,
        val isDebuggable: Boolean,
        val isDeviceSecure: Boolean,
        val appSignature: String?
    ) {
        val isSecure: Boolean
            get() = !isRooted && !isEmulator && !isDebuggable && isDeviceSecure
    }
}
