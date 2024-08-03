package com.theayushyadav11.messease.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.models.Poll
import com.theayushyadav11.messease.utils.Mess

class McMenuViewModel(application: Application) : AndroidViewModel(application) {

    var polls = MutableLiveData<List<Poll>>()
    private lateinit var auth: FirebaseAuth
    private lateinit var mess: Mess
    private lateinit var database: DatabaseReference

    init {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        mess = Mess(application)
        if (auth.currentUser != null) {
            getPolls()
        } else {
            // Handle case where user is not authenticated
        }
    }

    private fun getPolls() {
        val userId = auth.currentUser?.uid ?: return
        database.child("Users").child(userId).child("polls")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pollIds = snapshot.children.mapNotNull { it.value?.toString() }
                    mess.log(pollIds)
                    fetchPolls(pollIds)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun fetchPolls(ids: List<String>) {
        val pollsList = mutableListOf<Poll>()
        val fetchTasks = ids.map { id ->
            database.child("polls").child(id).get()
        }

        fetchTasks.forEach { task ->
            task.addOnSuccessListener { snapshot ->
                val poll = snapshot.getValue(Poll::class.java)
                if (poll != null) {
                    pollsList.add(poll)
                    pollsList.sortByDescending { it.date }
                }
                // Only update LiveData once all fetches are complete
                if (pollsList.size == ids.size) {
                    polls.postValue(pollsList)
                    mess.log(pollsList)
                }
            }.addOnFailureListener {
                // Handle failure
            }
        }
    }
}
