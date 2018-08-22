package com.example.aria.to_do_list.data


//
class ListData {
    constructor()

    //    var location: Int = 0
    var topic: String = ""
    var deadline: String = ""
    var notiTime: String = ""
    var notiMillis: Long = 0
    var content: String = ""
    var state: Boolean = false
    var key: Long = 0

    constructor(
//            location: Int,
            topic: String,
            deadline: String,
            notiTime: String,
            notiMillis: Long,
            content: String,
            state: Boolean,
            key: Long) {
//        this.location=location
        this.topic = topic
        this.deadline = deadline
        this.notiTime = notiTime
        this.notiMillis = notiMillis
        this.content = content
        this.state = state
        this.key = key
    }

//    override fun equals(other: Any?): Boolean {
//        if (other is ListData) {
//            return (dataEqual(other))
//        }
//        else return false
//    }
//
//    private fun dataEqual(other: ListData): Boolean {
//        return (topic == other.topic &&
//                deadline == other.deadline &&
//                notiTime == other.notiTime &&
//                notiMillis == other.notiMillis &&
//                content == other.content &&
//                state == other.state &&
//                key == other.key)
//    }
}

