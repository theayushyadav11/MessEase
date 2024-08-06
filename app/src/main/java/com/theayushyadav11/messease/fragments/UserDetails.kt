package com.theayushyadav11.messease.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.activities.MainActivity
import com.theayushyadav11.messease.databinding.FragmentUserDetailsBinding
import com.theayushyadav11.messease.utils.FireBase

import com.theayushyadav11.messease.utils.Mess
import java.util.Date

class UserDetails : Fragment() {
    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private var progressDialog: AlertDialog? = null
    lateinit var mess: Mess


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        addAdapter()
        addDetails()


    }

    fun initialize() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        mess = Mess(requireContext())
    }

    fun addDetails() {


        binding.done.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val passingYear = binding.auto.text.toString().trim()
            val gender = binding.autoGender.text.toString().trim()
            val batch = binding.autoBatch.text.toString().trim()
            if (name.isNotEmpty()) {
                mess.addPb("Adding Details...")
                database.child("Users").child(auth.currentUser?.uid.toString()).child("details")
                    .setValue(FireBase.Detail(name, batch, gender, passingYear))
               .addOnCompleteListener {
                    if (it.isSuccessful) {
                        mess.toast("Added successfully")
                        mess.pbDismiss()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()

                    }
                    mess.pbDismiss()
                }
            } else {
                mess.toast("Name cannot be empty!")
            }
        }

    }

    fun addAdapter() {
        val year = Date().year + 1900
        var listOfYear = listOf(year, year + 1, year + 2, year + 3, year + 4, year + 5)
        val adapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, listOfYear)
        binding.auto.setAdapter(adapter)


        var listOfBatch = listOf("Btech", "Mtech", "M.B.A.", "MSc", "Phd")
        val Batchadapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, listOfBatch)
        binding.autoBatch.setAdapter(Batchadapter)

        val adapter2 =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, listOf("Male", "Female"))
        binding.autoGender.setAdapter(adapter2)

    }
}