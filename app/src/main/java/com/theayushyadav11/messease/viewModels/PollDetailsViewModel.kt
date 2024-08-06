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
import com.theayushyadav11.messease.utils.FireBase

class PollDetailsViewModel : ViewModel() {
   private val auth= FirebaseAuth.getInstance()
    private val database= FirebaseDatabase.getInstance().reference
    var count=MutableLiveData<Int>()
    var listofStr=MutableLiveData<MutableList<OptionSelected>>()
    val listofName= MutableLiveData<MutableList<String>>()


    fun getPoll(id: String,onSuccess:(Poll)->Unit,onFailure:(String)->Unit)
    {
       database.child("polls").child(id).addValueEventListener(object : ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               val poll=snapshot.getValue(Poll::class.java)
               if (poll != null) {
                   Log.d("tag", poll.toString())
                   onSuccess(poll)
               }
               else
               onFailure("error getting poll")
           }

           override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
           }

       })
    }

    fun getoptionDetails(pollId:String,option: String,onSuccess:(Int,List<String>,List<OptionSelected>)->Unit,onFailure:(String)->Unit)
    {
        database.child("pollResult").child(pollId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val list= mutableListOf<OptionSelected>()
                val listofNames= mutableListOf<String>()
                var count=0
                for (data in snapshot.children)
                {
                    val optionSelected=data.getValue(OptionSelected::class.java)
                    if (optionSelected != null) {

                        if(optionSelected.selected==option)
                        {
                            count++
                            list.add(optionSelected)
                            listofNames.add(optionSelected.name)
                        }
                    }
                }
                onSuccess(count,listofNames,list)
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }






  }


