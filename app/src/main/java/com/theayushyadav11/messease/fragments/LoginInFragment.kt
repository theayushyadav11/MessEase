package com.theayushyadav11.messease.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.activities.MainActivity
import com.theayushyadav11.messease.databinding.FragmentLoginBinding
import com.theayushyadav11.messease.utils.Mess

class LoginInFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private var progressDialog: AlertDialog? = null
    lateinit var mess: Mess

    companion object {
        const val RC_SIGN_IN = 9001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
        animate()
        listeners()


    }

    fun initialise() {
        mess = Mess(requireContext())
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

    }

    fun listeners() {
        binding.btngsi.setOnClickListener {
            signIn()
        }
        binding.btnLogin.setOnClickListener {
            mess.addPb("Logging in...")
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            loginUser(email, password)
        }
        binding.tvForgotPassword.setOnClickListener {
            forgotPassword()
        }
        binding.tvSignUp.setOnClickListener {
            signUp()
        }
    }


    private fun signIn() {

        mess.addPb("Loading...")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mess.addPb("Signing in...")

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                if (account.email?.endsWith("@iiitl.ac.in") == true || true) {

                    firebaseAuthWithGoogle(account)
                } else {
                    mess.pbDismiss()
                    googleSignInClient.signOut()
                    Toast.makeText(
                        requireContext(),
                        "Login with college email id only",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } catch (e: ApiException) {
                mess.pbDismiss()
                Toast.makeText(
                    requireContext(),
                    "Google sign in failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkEmailAllowed(account: GoogleSignInAccount) {
        val email = account.email
        val list: MutableList<String> = mutableListOf()
        database.reference.child("allow").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (user in snapshot.children) {
                    list.add(user.value.toString())
                }
                if (list.any { it == email }) {
                    firebaseAuthWithGoogle(account)
                } else {
                    mess.pbDismiss()
                    googleSignInClient.signOut()
                    Toast.makeText(
                        requireContext(),
                        "Not a member of mess Committee.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                mess.pbDismiss()
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                mess.pbDismiss()
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    Toast.makeText(
                        requireContext(),
                        "Authentication Successful.",
                        Toast.LENGTH_SHORT
                    ).show()
                    checkEmailAllowed()

                    navigate()

                } else {

                    Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }


    private fun display(s: Any) {
        Snackbar.make(binding.textInputLayout, s.toString(), Snackbar.LENGTH_LONG).show()

    }

    private fun forgotPassword() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isNotEmpty()) {
            mess.addPb("Sending Password reset email...")
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    mess.pbDismiss()
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Password reset email sent",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            Snackbar.make(binding.root, "Email cannot be empty!", Snackbar.LENGTH_LONG).show()
            mess.pbDismiss()
        }
    }

    fun animate() {
        val imageView: ImageView = binding.imageView
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom)
        imageView.startAnimation(animation)
    }

    fun signUp() {
        findNavController().navigate(R.id.action_loginInFragment_to_signUpFragment)
    }

    private fun loginUser(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    mess.pbDismiss()
                    if (task.isSuccessful) {
                        // Check if email is verified
                        val user = auth.currentUser
                        if (user?.isEmailVerified == true) {
                            Toast.makeText(
                                requireContext(),
                                "Email is verified",
                                Toast.LENGTH_SHORT
                            ).show()
                            checkEmailAllowed()
                            navigate()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please verify your email address",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {

                        Toast.makeText(
                            requireContext(), task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Any feilds cannot be empty", Toast.LENGTH_SHORT)
                .show()
            mess.pbDismiss()
        }
    }

    private fun checkEmailAllowed() {
        try {
            val email = auth.currentUser?.email?.trim()
            val list: MutableList<String> = mutableListOf()
            database.reference.child("allow").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (user in snapshot.children) {
                        if (!(email == user.value.toString().trim())) {

                            Mess(requireContext()).setIsMember(false)

                        } else {
                            Mess(requireContext()).setIsMember(true)
                            break
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {


                }
            })
            Log.d("Yatin", list.toString())
        } catch (e: Exception) {

        }


    }

    fun navigate() {
        database.reference.child("Users").child(auth.currentUser?.uid.toString()).child("details")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        findNavController().navigate(R.id.action_loginInFragment_to_userDetails)
                    } else {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}