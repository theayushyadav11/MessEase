package com.theayushyadav11.messease.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.models.AprMenu
import com.theayushyadav11.myapplication.models.DayMenu

class UploadMenuViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    private val _menuList = MutableLiveData<List<AprMenu>>()
    val t = MutableLiveData<String>()
    val menuList: LiveData<List<AprMenu>> get() = _menuList

    init {
        fetchMenu()
    }

    private fun fetchMenu() {
        database.child("forApproval").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val aprMenuList: MutableList<AprMenu> = mutableListOf()
                for (child in snapshot.children) {
                    Log.d(TAG, child.value.toString())
                    val aprMenu = child.getValue(AprMenu::class.java)
                    aprMenu?.let { aprMenuList.add(aprMenu) }
                    aprMenuList.sortByDescending { it.date }
                }
                _menuList.value = aprMenuList
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun deleteApprove(key: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        database.child("forApproval").child(key).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess("Deleted")
            } else {
                onFailure(it.exception?.message.toString())
            }
        }
    }

    fun uploadMainMenu(menu: Menu2,onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        database.child("MainMenu").setValue(menu).addOnCompleteListener {
            if (it.isSuccessful) {
               onSuccess( "Menu uploaded successfully")
            } else {
                onFailure( it.exception?.message.toString())
            }
        }
    }

    fun uploadMainMenuUrl(url: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        database.child("ayush").setValue(url).addOnCompleteListener {
            if(it.isSuccessful) {
                onSuccess( "Menu uploaded successfully")
            }
            else
            {
                onFailure( it.exception?.message.toString())
            }
        }

    }
    fun getMainMenuUrl(onSuccess: (String) -> Unit, onFailure: (String) -> Unit)
    {
        database.child("ayush").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               onSuccess(snapshot.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
             onFailure(error.message)
            }

        })

    }


}

data class Menu2(
    val id: String = "",
    val menu: DayMenu = DayMenu()
)
