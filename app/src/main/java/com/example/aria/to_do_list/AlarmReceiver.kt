package com.example.aria.to_do_list

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import android.app.PendingIntent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val itemData = Gson().fromJson(intent.getStringExtra("itemData"), ListData::class.java)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val it = Intent(context, ToDoList_Activity::class.java)
        it.putExtra("notification", true)
        it.putExtra("itemData", intent.getStringExtra("itemData"))

        val pendingIntent = PendingIntent.getActivity(context, intent.getIntExtra("i", 0), it, PendingIntent.FLAG_ONE_SHOT)
        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("TodoList", "TodoList", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context, "TodoList")
        } else {
            builder = Notification.Builder(context)
        }

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(itemData.Topic)
                .setContentText("Deadline : " + itemData.Date + " " + itemData.Time)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        manager.notify(intent.getIntExtra("i", 0), builder.build())

    }

}


