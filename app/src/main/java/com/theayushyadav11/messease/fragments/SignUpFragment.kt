package com.theayushyadav11.messease.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
 private lateinit var binding:FragmentSignUpBinding
 private lateinit var auth: FirebaseAuth
 private lateinit var email: String
 private lateinit var password: String
override  fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSignUpBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        animate()
        listeners()


    }

    private fun initialise() {
        auth=FirebaseAuth.getInstance()
         email = binding.etEmail.text.toString().trim()
         password = binding.etPassword.text.toString().trim()
    }

    fun animate() {
        val imageView: ImageView = binding.imageView
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom)
        imageView.startAnimation(animation)
    }
    fun listeners()
    {
        binding.verify.setOnClickListener{
           // sendOtp(binding.etEmail.text.toString().trim())
            email = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString().trim()
            registerUser(email,password)

        }
        binding.tvSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_signUpFragment_to_loginInFragment)
        }
    }

//    private fun sendOtp(email: String) {
//        val data = hashMapOf("email" to email)
//
//        FirebaseFunctions.getInstance()
//            .getHttpsCallable("sendOtp")
//            .call(data)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // OTP sent successfully
//                    Toast.makeText(requireContext(), "OTP sent to your email.", Toast.LENGTH_SHORT).show()
//                    findNavController().navigate(R.id.action_signUpFragment_to_otpFragment)
//                } else {
//                    // Handle error
//                    Toast.makeText(requireContext(), "Failed to send OTP.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Registration successful, send verification email
                    sendVerificationEmail()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireContext(), task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun sendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Verification email sent to ${user.email}",
                        Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signUpFragment_to_loginInFragment)
                } else {
                    Toast.makeText(requireContext(), "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}