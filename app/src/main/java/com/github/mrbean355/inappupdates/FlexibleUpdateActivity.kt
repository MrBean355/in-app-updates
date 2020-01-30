package com.github.mrbean355.inappupdates

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_main.*

private const val UPDATE_REQUEST_CODE = 123

class FlexibleUpdateActivity : AppCompatActivity(R.layout.activity_main) {

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appUpdateManager.registerListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateDownloadedSnackbar()
            }
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.FLEXIBLE, this, UPDATE_REQUEST_CODE)
            }
        }.addOnFailureListener {
            Log.e("FlexibleUpdateActivity", "Failed to check for update: $it")
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateDownloadedSnackbar()
            }
        }
    }

    private fun showUpdateDownloadedSnackbar() {
        Snackbar.make(content, "Update downloaded!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install") { appUpdateManager.completeUpdate() }
                .show()
    }
}
