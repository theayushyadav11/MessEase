package com.theayushyadav11.messease.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.ActivityLoggedInBinding
import com.theayushyadav11.messease.utils.FireBase
import com.theayushyadav11.messease.utils.Mess

class LoggedIn : AppCompatActivity() {
    private lateinit var binding: ActivityLoggedInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mess: Mess
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        isMember()
        listeners()

    }

    private fun initialise() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding.root.isVisible = false
        mess = Mess(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.loginsignuphost)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun isMember() {
        mess.addPb("Checking your membership status...")
        FireBase().isMember(auth.currentUser?.uid.toString(), onSuccess = {
            mess.pbDismiss()
            if (!it) {
                binding.root.isVisible = false
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Alert!")
                builder.setCancelable(false)
                builder.setMessage("You are not a member of Mess Committee!")
                builder.setPositiveButton("Ok") { dialog, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    dialog.dismiss()
                }

                builder.show()
            } else {
                mess.pbDismiss()
                binding.root.isVisible = true
            }
        }, onFailure = {
            mess.pbDismiss()
            binding.root.isVisible = false
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert!")
            builder.setCancelable(false)
            builder.setMessage("Failed to get details!")
            builder.setPositiveButton("Ok") { dialog, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                dialog.dismiss()
            }
            builder.show()
        })

    }

    fun listeners() {

    }


}