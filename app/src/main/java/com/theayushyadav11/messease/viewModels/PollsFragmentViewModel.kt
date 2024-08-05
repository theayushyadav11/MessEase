package com.theayushyadav11.messease.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.models.OptionSelected
import com.theayushyadav11.messease.models.Poll

class PollsFragmentViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val toast = MutableLiveData<String>()

    init {

    }

    fun getYourPollUidList(onSuccess: (polls: List<String>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        database.child("Users").child(userId).child("polls")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pollIds = snapshot.children.mapNotNull { it.value.toString() }
                    Log.d(":chell", pollIds.toString())
                    onSuccess(pollIds)
                }

                override fun onCancelled(error: DatabaseError) {
                    toast.value = (error.message)
                }
            })
    }

    fun deletePoll(uid: String, onSuccess: () -> Unit, onFailure: () -> Unit) {




      database.child("Users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("polls")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (p in snapshot.children) {
                        try {
                            if (p.value.toString() == uid) {
                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                    .child("polls").child(p.key.toString()).removeValue()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            database.child("pollResult").child(uid)
                                                .removeValue().addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        database.child("polls").child(uid)
                                                            .removeValue().addOnCompleteListener {
                                                                if (it.isSuccessful) {
                                                                    onSuccess()
                                                                } else {
                                                                    onFailure()
                                                                }
                                                            }
                                                    } else {
                                                        onFailure()
                                                    }
                                                }
                                        } else {
                                            onFailure()
                                        }


                                    }
                                onSuccess()
                                break
                            }
                        } catch (e: Exception) {
                            onFailure()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure()
                }

            })


//        database.child("Users").child(auth.currentUser?.uid.toString()).child("polls").child(uid)
//            .removeValue().addOnCompleteListener {
//            if (it.isSuccessful) {
//                database.child("polls").child(uid).removeValue().addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        database.child("pollResult").child(uid).removeValue()
//                            .addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    onSuccess()
//                                } else {
//                                    onFailure()
//                                }
//                            }
//                    } else {
//                        onFailure()
//                    }
//                }
//            } else {
//                onFailure()
//            }
//        }


    }

    fun getPolls(onSuccess: (List<Poll>) -> Unit, onFailure: () -> Unit) {
        getYourPollUidList { polls ->
            val pollsList = mutableListOf<Poll>()
            for (pollId in polls) {
                database.child("polls").child(pollId)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val poll = snapshot.getValue(Poll::class.java)
                            Log.d(":chell", poll.toString())
                            if (poll != null) {
                                pollsList.add(poll)
                                pollsList.sortByDescending { it.comp }
                            }

                            onSuccess(pollsList)

                        }

                        override fun onCancelled(error: DatabaseError) {
                            onFailure()
                        }

                    })
            }


        }
    }

    fun getoptCount(uid: String, option: String, onSuccess: (Int, Int) -> Unit) {
        database.child("pollResult").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    var tv = 0
                    for (child in snapshot.children) {
                        tv++
                        val optionSelected = child.getValue(OptionSelected::class.java)
                        if (optionSelected?.selected == option)
                            count++
                    }
                    onSuccess(count, tv)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}