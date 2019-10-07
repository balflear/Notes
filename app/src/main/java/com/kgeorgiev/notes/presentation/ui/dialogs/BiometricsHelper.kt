package com.kgeorgiev.notes.presentation.ui.dialogs

import android.app.KeyguardManager
import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.os.Build
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.FragmentActivity
import com.kgeorgiev.notes.R
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * Created by kostadin.georgiev on 7/19/2019.
 */
class BiometricsHelper {
    companion object {
        private val executor: Executor by lazy {
            Executors.newSingleThreadExecutor()
            //MainThreadExecutor()
        }

        fun showBiometricsPrompt(
            activity: FragmentActivity,
            callback: androidx.biometric.BiometricPrompt.AuthenticationCallback
        ) {
            val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.title_authentication))
                .setDescription(activity.getString(R.string.msg_authenticate_first))
                .setDeviceCredentialAllowed(true)
                .build()

            val biometricPrompt =
                androidx.biometric.BiometricPrompt(
                    activity,
                    executor, callback
                )
            biometricPrompt.authenticate(promptInfo)
        }

        /**
         * Indicate whether this device can authenticate the user with biometrics
         * @return true if there are any available biometric sensors and biometrics are enrolled on the device, if not, return false
         */
        fun canAuthenticateWithBiometrics(context: Context): Boolean {
            // Check whether the fingerprint can be used for authentication (Android M to P)
            if (Build.VERSION.SDK_INT < 29) {
                val keyguardManager =
                    context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val fingerprintManagerCompat = FingerprintManagerCompat.from(context)
                return (fingerprintManagerCompat.isHardwareDetected && fingerprintManagerCompat.hasEnrolledFingerprints())
                        || keyguardManager.isDeviceSecure
            } else {    // Check biometric manager (from Android Q)
                val biometricManager = context.getSystemService(BiometricManager::class.java)
                if (biometricManager != null) {
                    return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                } else {
                    return false
                }
            }
        }
    }
}
