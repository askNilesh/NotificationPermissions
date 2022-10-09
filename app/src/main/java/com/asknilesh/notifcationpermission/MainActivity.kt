package com.asknilesh.notifcationpermission

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.asknilesh.notifcationpermission.R.style
import com.asknilesh.notifcationpermission.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

  private val notificationPermissionLauncher =
    registerForActivityResult(RequestPermission()) { isGranted ->
      hasNotificationPermissionGranted = isGranted
      if (!isGranted) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
          if (VERSION.SDK_INT >= 33) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
              showNotificationPermissionRationale()
            } else {
              showSettingDialog()
            }
          }
        }
      } else {
        Toast.makeText(applicationContext, "notification permission granted", Toast.LENGTH_SHORT)
          .show()
      }
    }

  private fun showSettingDialog() {
    MaterialAlertDialogBuilder(this, style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
      .setTitle("Notification Permission")
      .setMessage("Notification permission is required, Please allow notification permission from setting")
      .setPositiveButton("Ok") { _, _ ->
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  private fun showNotificationPermissionRationale() {

    MaterialAlertDialogBuilder(this, style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
      .setTitle("Alert")
      .setMessage("Notification permission is required, to show notification")
      .setPositiveButton("Ok") { _, _ ->
        if (VERSION.SDK_INT >= 33) {
          notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  lateinit var binding: ActivityMainBinding
  var hasNotificationPermissionGranted = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btnRequestPermission.setOnClickListener {
      if (VERSION.SDK_INT >= 33) {
        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
      } else {
        hasNotificationPermissionGranted = true
      }
    }
    binding.btnShowNotification.setOnClickListener {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        showNotification()
      }
    }
  }

  private fun showNotification() {

    val channelId = "12345"
    val description = "Test Notification"

    val notificationManager =
      getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      val notificationChannel =
        NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
      notificationChannel.lightColor = Color.BLUE

      notificationChannel.enableVibration(true)
      notificationManager.createNotificationChannel(notificationChannel)

    }

   val  builder = NotificationCompat.Builder(this, channelId)
      .setContentTitle("Hello World")
      .setContentText("Test Notification")
      .setSmallIcon(R.drawable.ic_android_black_24dp)
      .setLargeIcon(
        BitmapFactory.decodeResource(
          this.resources, R.drawable
            .ic_launcher_background
        )
      )
    notificationManager.notify(12345, builder.build())
  }
}