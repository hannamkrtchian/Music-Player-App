package com.example.musicplayerapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val navController = navHostFragment?.findNavController()

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        if (navController != null) {
            setupActionBarWithNavController(navController, appBarConfiguration)
        }
        if (navController != null) {
            navView.setupWithNavController(navController)
        }

        // get the recyclerview with the songs and the textview with "no songs"
        //val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_all_songs)
        //val textView = findViewById<TextView>(R.id.no_songs)

        // check API level to declare permission
        val permission: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For devices with API 33 (Tiramisu) and higher, use READ_MEDIA_AUDIO
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            // For devices with API levels between 24 and 32, use READ_EXTERNAL_STORAGE
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        // check and request permission
        if (!checkPermission(permission)) {
            requestPermission(permission)
            return
        }
    }

    private fun checkPermission(permission: String): Boolean {
        // Check permission
        val result = ContextCompat.checkSelfPermission(this, permission)
        // Return true or false
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(this,
                "Permission is required to access music library, please allow from settings.",
                Toast.LENGTH_SHORT).show()
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), 123)
        }
    }
}