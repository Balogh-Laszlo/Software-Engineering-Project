package com.example.software_engineering_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var bottomNavBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = ""
        initializeViews()
        initBottomMenu()
    }

    private fun initializeViews() {
        bottomNavBar = findViewById(R.id.bottomNavigationView)
    }

    private fun initBottomMenu() {
        bottomNavBar.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.home -> findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
                R.id.new_party -> findNavController(R.id.nav_host_fragment).navigate(R.id.newPartyFragment)
                R.id.list_parties -> findNavController(R.id.nav_host_fragment).navigate(R.id.partyListFragment)
                else -> findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // TODO: Change destination fragments
            R.id.profile -> findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
            R.id.notifications -> findNavController(R.id.nav_host_fragment).navigate(R.id.notificationsFragment)
            else -> findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
        }
        return true
    }
}