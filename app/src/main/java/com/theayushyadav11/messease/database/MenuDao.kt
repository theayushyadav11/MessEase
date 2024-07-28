package com.theayushyadav11.myapplication.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.theayushyadav11.myapplication.models.Menu

@Dao
interface MenuDao {

    @Upsert
    suspend fun addMenu(menu: Menu)

    @Query("SELECT *FROM menu Where id =\"1\"")
    suspend fun getMenu():Menu

    @Query("SELECT *FROM menu Where id =\"edited\"")
    suspend fun getEditedMenu():Menu

}