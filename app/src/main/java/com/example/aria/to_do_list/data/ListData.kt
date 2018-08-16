package com.example.aria.to_do_list.data

import java.sql.Time
import java.util.*


//
class ListData  {
    constructor()
    var Location: Int = 0
    var Topic: String = ""
    var Deadline: String = ""
    var NotiTime: String = ""
    var NotifyTime: Long = 0
    var Content: String = ""
    var State: Boolean = false
    var SaveTime: Long = 0

    constructor(Location: Int,
                Topic: String,
                Deadline: String,
                NotiTime: String,
                NotifyTime: Long,
                Content: String,
                State: Boolean,
                SaveTime: Long){
        this.Location=Location
        this.Topic=Topic
        this.Deadline=Deadline
        this.NotiTime=NotiTime
        this.NotifyTime=NotifyTime
        this.Content=Content
        this.State=State
        this.SaveTime=SaveTime
    }


}
