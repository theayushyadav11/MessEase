package com.theayushyadav11.messease.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.theayushyadav11.messease.R

class LoginSignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_sign_up)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.loginsignuphost) as NavHostFragment
        val navController = navHostFragment.navController

if(intent.getIntExtra("ll",0)==1)
{
    val navInflater = navController.navInflater
    val graph = navInflater.inflate(R.navigation.nav_graph_login_sign_up)
    graph.setStartDestination( R.id.userDetails)
    navController.graph = graph
}

    }
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.loginsignuphost)
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}