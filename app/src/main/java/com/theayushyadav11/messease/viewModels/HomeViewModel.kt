package com.theayushyadav11.messease.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.models.Poll
import com.theayushyadav11.myapplication.models.Menu
import com.theayushyadav11.myapplication.models.Particulars
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel() : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    val currentDate = MutableLiveData<Int>()
    val dayOfWeek = MutableLiveData<Int>()
    val currentMonthYear = MutableLiveData<String>()
    val menuList = MutableLiveData<MutableList<Particulars>>()
    private lateinit var menu: Menu

    init {
        _text.value = "Ayush Yadav is good"
        currentDate.value = getCurrentDayOfMonth()
        currentMonthYear.value = getCurrentMonthYear()
        dayOfWeek.value = getCurrentDayOfWeek()


    }

    private fun getCurrentDayOfMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun getCurrentMonthYear(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time).uppercase()
    }

    private fun getCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getTarget(
        id: String,
        onSuccess: (target: List<String>) -> Unit,
        onFailure: (target: String) -> Unit
    ) {
        database.child("Users").child(auth.uid.toString()).child("details")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var target: MutableList<String> = mutableListOf()
                    for (i in snapshot.children) {
                        Log.d("yatn", i.value.toString())
                        if (i.key.toString() != "name") {
                            target.add(i.value.toString())
                        }
                    }
                    Log.d("yatn", target.toString())
                    onSuccess(target)

                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.message)
                }

            })
    }

    fun getPolls(
        date: String,
        onSuccess: (polls: List<Poll>) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        database.child("polls").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pollList: MutableList<Poll> = mutableListOf()
                var lastKey: String = ""
                for (i in snapshot.children) {
                    lastKey=i.key.toString()
                }
                for (i in snapshot.children) {
                    val poll = i.getValue(Poll::class.java)
                    if (poll != null) {
                        getTarget(poll.uid, onSuccess = {
                            val check =
                                poll.target.contains(it[0]) && poll.target.contains(it[1]) && poll.target.contains(
                                    it[2]
                                ) && poll.date == date

                            if (check)
                                pollList.add(poll)
                            if(i.key==lastKey)
                            {
                                onSuccess(pollList)
                            }
                        }, onFailure = {
                            onFailure("Some Error occurred")
                        })
                    }
                }
                Log.d("yatn", pollList.toString())
                onSuccess(pollList)

            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }

        })
    }

}
