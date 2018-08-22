package com.example.aria.to_do_list

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.gson.Gson
import android.app.PendingIntent
import com.example.aria.to_do_list.data.ListData
import com.example.aria.to_do_list.main.ToDoList_Activity


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val itemData = Gson().fromJson(intent.getStringExtra("orgItemData"), ListData::class.java)
        val i = intent.getIntExtra("i", 0)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val it = Intent(context, ToDoList_Activity::class.java)
        it.putExtra("notification", true)
        it.putExtra("orgItemData", intent.getStringExtra("orgItemData"))

//        val pendingIntent = PendingIntent.getActivity(context, intent.getIntExtra("i", 0), it, PendingIntent.FLAG_ONE_SHOT)
        val pendingIntent = PendingIntent.getActivity(context, i, it, PendingIntent.FLAG_ONE_SHOT)

        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("TodoList", "TodoList", NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context, "TodoList")
        } else {
            builder = Notification.Builder(context)
        }

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(itemData.topic)
                .setContentText("deadline : " + itemData.deadline)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        manager.notify(i, builder.build())
    }
}


