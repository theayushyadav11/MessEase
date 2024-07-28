package com.theayushyadav11.messease.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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




}