package com.example.aria.to_do_list

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.gson.Gson
import android.app.PendingIntent
import android.util.Log
import com.example.aria.to_do_list.data.ListData
import com.example.aria.to_do_list.main.ToDoList_Activity


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val itemData = Gson().fromJson(intent.getStringExtra("itemData"), ListData::class.java)
        val i = intent.getIntExtra("i", 0)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val it = Intent(context, ToDoList_Activity::class.java)
        it.putExtra("notification", true)
        it.putExtra("itemData", intent.getStringExtra("itemData"))

        val pendingIntent = PendingIntent.getActivity(context, i, it, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("TodoList", "TodoList", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context, "TodoList")
        } else {
            builder = Notification.Builder(context)
        }

        val deadline = if (itemData.deadline=="  ") "No Deadline" else itemData.deadline

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(itemData.topic)
                .setContentText("deadline : " + deadline)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        manager.notify(i, builder.build())
    }
}


