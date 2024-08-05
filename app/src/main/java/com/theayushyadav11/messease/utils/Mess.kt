package com.theayushyadav11.messease.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.myapplication.database.MenuDatabase
import com.theayushyadav11.myapplication.models.Menu
import kotlinx.coroutines.DelicateCoroutinesApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Mess {
    lateinit var context: Context
    lateinit var progressDialog: ProgressDialog
    lateinit var sharedPreferences: SharedPreferences
    val disign = MutableLiveData<String>()

    constructor(context: Context) {

        progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        init()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (context != null) {
            this.context = context
        }
    }

    fun init() {
        currentDesgin()
    }

    fun save(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun get(key: String): String {
        return sharedPreferences.getString(key, "").toString()
    }

    fun sendPollId(id: String)
    {
        save("pollId", id)
    }

    fun getPollId():String
    {
        return get("pollId")
    }
    fun setIsMember(isMember: Boolean) {
        save("isMember", isMember.toString())
    }

    fun isMember(): Boolean {

        if (get("isMember") == "true") {
            return true
        } else {
            return false
        }


    }

    fun addPb(message: String) {
        progressDialog.dismiss()
        progressDialog.setMessage(message)
        progressDialog.show()

    }

    fun pbDismiss() {
        progressDialog.dismiss()
    }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun log(message: Any) {
        Log.d("yatinMAdharchod", message.toString())
    }

    fun currentDesgin() {
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference
        database.child("Users").child(auth.currentUser?.uid.toString()).child("designation")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    disign.value = snapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getCurrentMenu(): Menu {
        val database = MenuDatabase.getDatabase(context).menuDao()
        return database.getMenu()
    }

    fun getCurrentTimeInAmPm(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("${Date().date}/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getCurrentTimeAndDate(): String {
        val dateFormat = SimpleDateFormat("hh:mm a dd MMM yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}