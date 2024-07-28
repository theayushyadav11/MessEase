package com.theayushyadav11.myapplication.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theayushyadav11.myapplication.models.DayMenu

class Converters {

    @TypeConverter
    fun fromDayMenu(dayMenu: DayMenu): String {
        return Gson().toJson(dayMenu)
    }

    @TypeConverter
    fun toDayMenu(dayMenuString: String): DayMenu {
        val type = object : TypeToken<DayMenu>() {}.type
        return Gson().fromJson(dayMenuString, type)
    }
}
