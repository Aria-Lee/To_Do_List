package com.example.aria.to_do_list.data.Room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(ListData::class), version = 1)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun ToDoDao(): ToDoDao
    companion object {
        val DBNAME = "TEST"
        private var INSTANCE: ToDoDatabase? = null
        fun getInstance(context: Context): ToDoDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(context,
                                ToDoDatabase::class.java,
                                ToDoDatabase.DBNAME)
                        .build()
            }
            return INSTANCE
        }
        fun destoryInstance() {
            if (INSTANCE != null)
                INSTANCE!!.close()
            INSTANCE = null
        }
    }
}