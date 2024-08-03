package com.theayushyadav11.messease.models

import java.util.Date

data class Msg(
    val uid: String = "",
    val creater: String = "",
    val time: String = "",
    val date: String = "",
    val comp: Date = Date(),
    val title: String = "",
    val body: String = "",
    val photos: List<String>,
    val target: String = "",
)
