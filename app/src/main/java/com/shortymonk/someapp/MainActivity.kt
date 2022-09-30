package com.shortymonk.someapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

const val PERMISSION_REQUEST_CODE = 2128
const val NOTIFICATION_ID = 8212

class MainActivity : AppCompatActivity() {

    private var isRestart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val builder = AppBarConfiguration.Builder(setOf(
            R.id.homeFragment,
            R.id.friendsFragment,
            R.id.helpFragment
        ))
        builder.setOpenableLayout(drawer)

        val appBarConfiguration = builder.build()
        val navView = findViewById<NavigationView>(R.id.nav_view)

        toolbar.setupWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController =findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        val isGranted = ContextCompat.checkSelfPermission(
            this,
            HomeFragment.PERMISSION_READ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(HomeFragment.PERMISSION_READ),
                PERMISSION_REQUEST_CODE
            ).also { Log.d("SomeAppPermission", "permission is required") }
        }
        if (isRestart && isGranted) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
            isRestart = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("SomeAppPermission", "Permission is granted")
                    findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
                } else {
                    showNotification()
                    Log.d("SomeAppPermission", "Permission isn't granted")
                }
            }
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_ID.toString()).apply {
            setSmallIcon(android.R.drawable.sym_def_app_icon)
            setContentTitle(resources.getString(R.string.app_name))
            setContentText(resources.getString(R.string.permission_denied))
            priority = NotificationCompat.PRIORITY_HIGH
            setVibrate(longArrayOf(0, 3))
            setAutoCancel(true)
        }

        val uri = Uri.fromParts("package", packageName, null)
        val actionIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val actionPendingIntent = PendingIntent.getActivity(
            this,
            0,
            actionIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        builder.setContentIntent(actionPendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val name = "permission request"
        val descriptionText = "SomeApp_permission"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(NOTIFICATION_ID.toString(), name, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
        isRestart = true
    }

}