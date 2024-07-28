package com.theayushyadav11.messease.models

import java.util.Date

data class Poll (
    val uid:String="",
    val creater:String="",
    val question:String="",
    val date: String ="",
    val time: String ="",
    var totalVotes:Int=0,
    var isMultiple:Boolean=false,
    val options:MutableList<String> = mutableListOf()


    )

data class Option(
    val value:String="",
)