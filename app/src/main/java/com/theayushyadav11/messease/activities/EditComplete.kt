package com.theayushyadav11.messease.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.databinding.ActivityEditCompleteBinding
import com.theayushyadav11.messease.databinding.EditDialogBinding
import com.theayushyadav11.messease.models.AprMenu
import com.theayushyadav11.messease.utils.FireBase
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.Menu2
import com.theayushyadav11.myapplication.database.MenuDatabase
import com.theayushyadav11.myapplication.models.Menu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class EditComplete : AppCompatActivity() {
    private lateinit var binding: ActivityEditCompleteBinding
    private lateinit var mess: Mess
    private lateinit var editedMenu: Menu
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        listener()
    }
    fun initialise() {
        mess = Mess(this)
        val fileName = "Mess Menu.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (file.exists()) {
            uri = FileProvider.getUriForFile(
                this,
                "com.theayushyadav11.messease.provider",
                file
            )
        }}
        fun listener() {
            binding.button.setOnClickListener {
                openPDF()
            }
            binding.send.setOnClickListener {
                showInputDialog()
            }
            binding.imageView5.setOnClickListener {
                onBackPressed()
            }
        }

        fun openPDF() {


            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.d("PDF", e.toString())
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }

        }

        fun sendToCorrdi(note: String, creater: String) {
            mess.addPb("Sending...")
            val key = FirebaseDatabase.getInstance().reference.push().key.toString()
            FireBase().uploadPdfToFirebase(0,uri,key, onSuccess ={pdfurl->

                val roomDatabase = MenuDatabase.getDatabase(this).menuDao()
                lifecycleScope.launch(Dispatchers.IO) {
                    editedMenu = roomDatabase.getEditedMenu()

                    mess.log(editedMenu)

                    val aprMenu =
                        AprMenu(
                            key, note,url=pdfurl, creater, date = Date(),
                            menu = Menu2(editedMenu.id, editedMenu.menu), displayDate = mess.getCurrentTimeAndDate()
                        )

                    FirebaseDatabase.getInstance().reference.child("forApproval").child(key)
                        .setValue(aprMenu)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                mess.toast("Menu send for approval.")
                                GlobalScope.launch(Dispatchers.IO) {
                                    val databse = MenuDatabase.getDatabase(this@EditComplete).menuDao()
                                    databse.addMenu(editedMenu)
                                }
                            } else {
                                mess.toast(it.exception?.message.toString())
                            }

                        }


                }




mess.pbDismiss()
                binding.send.isVisible=false
            },
                onFailure ={exception ->

              mess.toast(exception.message.toString())
                    mess.pbDismiss()

            })














        }

        fun showInputDialog() {
            val dialog = Dialog(this)
            val bind = EditDialogBinding.inflate(layoutInflater)

            dialog.setContentView(bind.root)
            dialog.setCancelable(false)
            dialog.show()
            bind.textInputLayout3.hint = "Add Note"
            bind.cancel.setOnClickListener {
                dialog.dismiss()
            }
            bind.done.setOnClickListener {
                if (bind.etUpdate.text.toString().trim().isNotEmpty()) {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .child("details").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            var name = ""
                            var year = ""
                            for (i in snapshot.children) {
                                Log.d("pranilash", i.toString())
                                if (i.key == "name")
                                    name = i.value.toString()
                                if (i.key == "passYear")
                                    year = i.value.toString()
                            }

                            sendToCorrdi(bind.etUpdate.text.toString().trim(), "$name \n $year")

                            dialog.dismiss()
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                } else {
                    mess.toast("Cannot add empty item.")
                }
            }
        }
    }