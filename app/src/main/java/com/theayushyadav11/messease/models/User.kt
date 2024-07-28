package com.theayushyadav11.messease.models

import com.theayushyadav11.myapplication.models.Menu
import java.util.Date


data class User(

    val name: String = "",
    val email: String = "",
    val password:String="",
    val designation: String = ""
)
data class AprMenu(
    val creater: String = "",
    val menu: Menu? =null,
    val date: Date =Date()
)
