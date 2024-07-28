package com.theayushyadav11.messease.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.ActivityEditCompleteBinding
import com.theayushyadav11.messease.models.AprMenu
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.myapplication.database.MenuDatabase
import com.theayushyadav11.myapplication.models.Menu
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class EditComplete : AppCompatActivity() {
    private lateinit var binding:ActivityEditCompleteBinding
    private lateinit var mess: Mess
    private lateinit var editedMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=ActivityEditCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        listener()




    }
    fun initialise()
    {
        mess=Mess(this)
    }
    fun listener()
    {
        binding.button.setOnClickListener{
            openPDF()
        }
        binding.send.setOnClickListener{
            sendToCorrdi()
        }
        binding.imageView5.setOnClickListener{
            onBackPressed()
        }
    }
    private fun openPDF() {
        val fileName = "Mess Menu.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (file.exists()) {
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "com.theayushyadav11.messease.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.d("PDF", e.toString())
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
        }
    }
    fun sendToCorrdi()
    {
        mess.addPb("Sending...")
       val roomDatabase=MenuDatabase.getDatabase(this).menuDao()
        lifecycleScope.launch(Dispatchers.IO) {
         editedMenu=roomDatabase.getEditedMenu()
            mess.log(editedMenu)
            val aprMenu=AprMenu(FirebaseAuth.getInstance().currentUser?.uid.toString(),editedMenu)
       FirebaseDatabase.getInstance().reference.child("forApproval").push().setValue(aprMenu).addOnCompleteListener{
             if(it.isSuccessful)
             {
                 mess.toast("Menu send for approval.")
             }
             else
             {
                 mess.toast(it.exception?.message.toString())
             }
             mess.pbDismiss()


         }



        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MenuMaking::class.java))
    }
}