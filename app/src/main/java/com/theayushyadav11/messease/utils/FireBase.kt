package com.theayushyadav11.messease.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theayushyadav11.messease.models.Msg
import com.theayushyadav11.messease.models.Poll

class FireBase {
    val database = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()


    fun <T> addData(data: T, onComplete: (Boolean, Exception?) -> Unit) {
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

    fun getTotalPollVotes(uid: String, onComplete: (Any?, Exception?) -> Unit) {
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

    fun incrementTotalVotes(uid: String, onComplete: (Boolean, Exception?) -> Unit) {
        val ref = database.child("polls").child(uid).child("totalVotes")
        getTotalPollVotes(uid) { value, e ->
            ref.setValue(value).addOnSuccessListener {
                onComplete(true, null)
            }
                .addOnFailureListener { e ->
                    onComplete(false, e)
                }

        }

    }

    fun uploadPdfToFirebase(
        v: Int,
        uri: Uri,
        key: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val pdfRef: StorageReference

        pdfRef = storageRef.child("ApprovePdf").child("MessMenu.pdf$key")


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

    fun deleteFileFromUrl(
        pdfUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

    fun uploadImages(
        id: String,
        images: List<Uri>,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (images.isEmpty())
            onSuccess(emptyList())
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageUrls = mutableListOf<String>()
        images.forEachIndexed { index, uri ->
            val imageRef = storageRef.child("MsgImages").child(id).child("image$index")
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrls.add(uri.toString())
                    if (imageUrls.size == images.size) {
                        onSuccess(imageUrls)
                    }
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }
    }

    fun addMsg(msg: Msg, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val ref = database.child("Messages").child(msg.uid)
        ref.setValue(msg).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun getDetails(uid: String, onSuccess: (name: String, designation: String) -> Unit) {
        database.child("UsersMc").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val designation = snapshot.child("designation").value.toString()
                onSuccess(name, designation)
            }

            override fun onCancelled(error: DatabaseError) {
                onSuccess("", "")
            }

        })
    }

    fun getDetailByUid(
        uid: String,
        onSuccess: (String, String, String, String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("Users").child(uid).child("details")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val detail = snapshot.getValue(Detail::class.java)
                    if (detail != null) {
                        onSuccess(detail.name, detail.batch, detail.passYear, detail.gender)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.message)
                }

            })
    }
    fun isMember(uid: String, onSuccess: (Boolean) -> Unit, onFailure: (String) -> Unit) {
        database.child("allow").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

               if(snapshot.value!=null)
                   onSuccess(true)
                 else
                onSuccess(false)

            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }
    fun getMcMemberDetail(
        uid: String,
        onSuccess: (String, String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("Users").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val designation = snapshot.child("designation").value.toString()
                onSuccess(name, designation)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })


    }

    data class Detail(
        val name: String = "",
        val batch: String = "",
        val gender: String = "",
        val passYear: String = ""
    )
}