package com.example.aria.to_do_list.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

//import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "ToDoList")
class ListData {

    @PrimaryKey(autoGenerate = true)
    var key: Long = 0
    @ColumnInfo(name = "topic")
    var topic: String = ""
    @ColumnInfo(name = "deadline")
    var deadline: String = ""
    @ColumnInfo(name = "notiTime")
    var notiTime: String = ""
    @ColumnInfo(name = "notiMillis")
    var notiMillis: Long = 0
    @ColumnInfo(name = "content")
    var content: String = ""
    @ColumnInfo(name = "state")
    var state: Boolean = false

    @Ignore constructor(
            topic: String,
            deadline: String,
            notiTime: String,
            notiMillis: Long,
            content: String,
            state: Boolean) {
        this.topic = topic
        this.deadline = deadline
        this.notiTime = notiTime
        this.notiMillis = notiMillis
        this.content = content
        this.state = state
    }

    constructor(
            key: Long,
            topic: String,
            deadline: String,
            notiTime: String,
            notiMillis: Long,
            content: String,
            state: Boolean) {
        this.key = key
        this.topic = topic
        this.deadline = deadline
        this.notiTime = notiTime
        this.notiMillis = notiMillis
        this.content = content
        this.state = state
    }

}