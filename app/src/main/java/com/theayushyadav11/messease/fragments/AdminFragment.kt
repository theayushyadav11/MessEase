package com.theayushyadav11.messease.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentAdminBinding
import com.theayushyadav11.messease.models.User
import com.theayushyadav11.messease.utils.Mess


class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databse: DatabaseReference
    private lateinit var designation: String
    private lateinit var pb: ProgressDialog
    private lateinit var mess:Mess
    override fun onCreate(savedInstanceState: Bundle?) {
        context?.theme?.applyStyle(
            com.google.android.material.R.style.Theme_Material3_Light,
            true
        ) // Apply the custom theme
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(layoutInflater, container, false)
        initialise()
        binding.btnAdd.setOnClickListener {
            add()
        }



















        return binding.root
    }

    fun initialise() {
        auth = FirebaseAuth.getInstance()
        mess=Mess(requireContext())
        databse = FirebaseDatabase.getInstance().reference
        spinner()
    }

    fun spinner() {


        val spinner: Spinner = binding.spinner

        val spinnerItems = listOf("Volunteer", "Member", "Senior-Member", "Coordinator")

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Step 5: Handle the item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                designation = selectedItem

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                designation = spinnerItems[0]
            }
        }
    }

    fun add() {
       mess.addPb("Adding a $designation...")
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
            registerUser(email, password)
        } else {
            Toast.makeText(requireContext(), "Cannot add empty feilds", Toast.LENGTH_SHORT).show()
        }
    }

    fun display(s: Any) {
        Snackbar.make(binding.textInputLayout, s.toString(), Snackbar.LENGTH_LONG).show()
        //Toast.makeText(this, s.toString(), Toast.LENGTH_SHORT).show()
    }

    fun addOnDatabase(email: String, password: String, name: String) {
        databse.child("Users").child(auth.currentUser?.uid.toString())
            .setValue(User(name, email, password, designation))
        databse.child("allow").push().setValue(email)

    }

    fun signOut() {


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth.signOut()

        mGoogleSignInClient.signOut()
    }


    private fun registerUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail()
                    display("User created successfully")
                    addOnDatabase(email, password, binding.etName.text.toString().trim())
                    signOut()
                    auth.signInWithEmailAndPassword("lit2023049@iiitl.ac.in","ayush1234").
                        addOnCompleteListener{
                            if(it.isSuccessful)
                            {
                                mess.pbDismiss()
                                display("Added Successfully")
                            }
                            else
                            {
                                mess.pbDismiss()
                                display("Failed to sign in")
                            }
                        }
                    binding.etEmail.setText("")
                    binding.etPassword.setText("")
                    binding.etName.setText("")


                } else {

                    Toast.makeText(
                        requireContext(), task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                mess.pbDismiss()
            }
    }

    private fun sendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(), "Verification email sent to ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(
                        requireContext(), "Failed to send verification email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}