package com.hfad.someapp

import android.content.ContentProvider
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.UserDictionary
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

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
//        val name = "testFile.txt"
//        val directory = this.getDir("testFiles", Context.MODE_PRIVATE)
//        val testFile = File(directory, name)
//
//        FileOutputStream(testFile)
//        val isFileExist = testFile.exists()
//        Log.d("checkPath", "${testFile.name} : $isFileExist")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController =findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }
}