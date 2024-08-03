package com.theayushyadav11.messease.models

import com.theayushyadav11.messease.viewModels.Menu2
import java.util.Date


data class User(

    val name: String = "",
    val email: String = "",
    val password: String = "",
    val designation: String = ""
)

data class AprMenu(
    val id: String = "",
    val note: String = "",
    val url: String = "",
    val creater: String = "",
    val menu: Menu2? = null,
    val date: Date = Date(),
    val displayDate: String = "",

    )
