package com.example.aria.to_do_list.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query

@Entity(tableName = "ToDoList")
class Room {

    var id :Long = 0
    var topic: String = ""
    var deadline: String = ""
    var notiTime: String = ""
    var notiMillis: Long = 0
    var content: String = ""
    var state: Boolean = false

    @Dao
interface ToDoListDao{

        

    }


}