package com.theayushyadav11.messease.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FireBase {
    val database=FirebaseDatabase.getInstance().reference
    val auth= FirebaseAuth.getInstance()


    fun <T> addData( data: T, onComplete: (Boolean, Exception?) -> Unit) {
        val ref = database.child("polls")
        ref.setValue(data)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
    fun <T> getData(path: String, clazz: Class<T>, onComplete: (T?, Exception?) -> Unit) {
        val ref = database.child("polls")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(clazz)
                onComplete(data, null)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null, error.toException())
            }
        })
    }
    fun getTotalPollVotes(uid:String, onComplete: (Any?, Exception?) -> Unit) {
        val ref = database.child("polls").child(uid).child("totalVotes")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                onComplete(value, null)
            }
            override fun onCancelled(error: DatabaseError) {
                onComplete(null, error.toException())
            }
        })
    }
    fun incrementTotalVotes(uid:String, onComplete: (Boolean, Exception?) -> Unit) {
        val ref =database.child("polls").child(uid).child("totalVotes")
        getTotalPollVotes(uid,){value,e->
            ref.setValue(value) .addOnSuccessListener {
                onComplete(true, null)
            }
                .addOnFailureListener { e ->
                    onComplete(false, e)
                }

        }

    }
    fun uploadPdfToFirebase(uri:Uri,key:String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val pdfRef = storageRef.child("ApprovePdf").child("MessMenu.pdf$key")

        val file = uri
        val uploadTask = pdfRef.putFile(file)

        uploadTask.addOnSuccessListener {
            pdfRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
    fun deletePdfFromFirebase(pdfUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl(pdfUrl)

        storageRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}