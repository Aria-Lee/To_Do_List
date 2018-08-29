package com.example.aria.to_do_list.data.Room

import android.arch.persistence.room.*
import com.example.aria.to_do_list.data.ListData

@Dao
interface ToDoDao {
    @Query("SELECT * from ToDoList")
    fun getAll(): List<ListData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data:ListData)

    @Query("SELECT * from ToDoList order by `key` desc limit 1")
    fun getData(): ListData

    @Delete
    fun delete(data:ListData)

    @Update
    fun update(data:ListData)
}