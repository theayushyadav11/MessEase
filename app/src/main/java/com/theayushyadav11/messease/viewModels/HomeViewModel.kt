package com.theayushyadav11.messease.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theayushyadav11.myapplication.models.Menu
import com.theayushyadav11.myapplication.models.Particulars
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel() : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    val currentDate = MutableLiveData<Int>()
    val dayOfWeek = MutableLiveData<Int>()
    val currentMonthYear = MutableLiveData<String>()
    val menuList = MutableLiveData<MutableList<Particulars>>()
    private lateinit var menu: Menu

    init {
        _text.value = "Ayush Yadav is good"
        currentDate.value = getCurrentDayOfMonth()
        currentMonthYear.value = getCurrentMonthYear()
        dayOfWeek.value=getCurrentDayOfWeek()


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

    private fun getMenu(day: Int): MutableList<Particulars> {
        return when (day) {
            Calendar.MONDAY -> menu.menu.Monday.toMutableList()
            Calendar.TUESDAY -> menu.menu.Tuesday.toMutableList()
            Calendar.WEDNESDAY -> menu.menu.Wednesday.toMutableList()
            Calendar.THURSDAY -> menu.menu.Thursday.toMutableList()
            Calendar.FRIDAY -> menu.menu.Friday.toMutableList()
            Calendar.SATURDAY -> menu.menu.Saturday.toMutableList()
            Calendar.SUNDAY -> menu.menu.Sunday.toMutableList()
            else -> mutableListOf()
        }
    }
}
