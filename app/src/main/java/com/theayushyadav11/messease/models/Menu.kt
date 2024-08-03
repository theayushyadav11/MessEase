package com.theayushyadav11.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Menu(
    @PrimaryKey
    val id: String,
    val menu: DayMenu = DayMenu()
)
