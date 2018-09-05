package com.example.aria.to_do_list

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.getSystemService

class ClearAlarm {
    fun cancelAlarm(context: Context,key: Long): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(context, (key % Int.MAX_VALUE).toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
        return true
    }
}