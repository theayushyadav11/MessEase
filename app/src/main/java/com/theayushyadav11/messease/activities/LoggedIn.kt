package com.theayushyadav11.messease.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.ActivityLoggedInBinding
import com.theayushyadav11.messease.utils.Mess

class LoggedIn : AppCompatActivity() {
    private lateinit var binding: ActivityLoggedInBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        isMember()
        listeners()

    }

    private fun initialise() {
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        binding.root.isVisible=false
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.loginsignuphost)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun isMember() {

        if(!Mess(this).isMember()) {

            binding.root.isVisible = false
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert!")
            builder.setCancelable(false)
            builder.setMessage("You are not a member of Mess Committee?")
            builder.setPositiveButton("Ok") { dialog, which ->
                startActivity(Intent(this, MainActivity::class.java))
                dialog.dismiss()
            }

            builder.show()
        }
        else
        {
            binding.root.isVisible = true
        }

    }

    fun listeners()
    {

    }


}