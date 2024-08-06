package com.theayushyadav11.messease.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.ActivityMainBinding
import com.theayushyadav11.messease.utils.Mess


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {

                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_review -> {

                Toast.makeText(this, "Write Review clicked", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun init() {
        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView: View = navView.getHeaderView(0)
        val  layout: LinearLayout = headerView.findViewById(R.id.navMain)
        val requestOptions = RequestOptions.circleCropTransform()
        Glide.with(this)
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .apply(requestOptions)
            .into( layout.findViewById<ImageView>(R.id.propic))
        layout.findViewById<TextView>(R.id.name).text = FirebaseAuth.getInstance().currentUser?.displayName
        layout.findViewById<TextView>(R.id.email).text = FirebaseAuth.getInstance().currentUser?.email









        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val menu: Menu = navView.menu
        val menuItem1 = menu.findItem(R.id.nav_messCommitteeActivity)
        val menuItem2 = menu.findItem(R.id.loggedin)
        val menuItem3 = menu.findItem(R.id.nav_admin)
        if (FirebaseAuth.getInstance().currentUser?.email == "lit2023049@iiitl.ac.in")
        {
            menuItem3.isVisible = true
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            menuItem1.isVisible = true
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_messCommitteeActivity,
                    R.id.nav_slideshow,
                    R.id.nav_admin,
                    R.id.nav_logout
                ), drawerLayout
            )
        } else {
            menuItem2.isVisible = true
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.loggedin,
                    R.id.nav_slideshow,
                    R.id.nav_admin,
                    R.id.nav_logout
                ), drawerLayout
            )
        }

        setSupportActionBar(binding.appBarMain.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    signOut()

                    true
                }
                R.id.nav_download->{
                    Mess(this).toast("Abhi ye feature add nhi hua hai")
                    true
                }
                else -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun signOut() {
        var mAuth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginSignUp::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()

        }
    }

    data class NotificationData(
        val hour: Int,
        val minute: Int,
        val title: String,
        val text: String,
        val id: Int
    )
}