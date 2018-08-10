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
//it.flags=FLAG_ACTIVITY_NEW_TASK

        val pendingIntent = PendingIntent.getActivity(context, intent.getIntExtra("i", 0), it, PendingIntent.FLAG_ONE_SHOT)
        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("ch1", "todolistChannel", NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context, "ch1")
        } else {
            builder = Notification.Builder(context)
        }

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(itemData.Topic)
                .setContentText("Deadline : " + itemData.Date + " " + itemData.Time)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(null,true)
//                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setAutoCancel(true)

//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder.setSmallIcon(R.drawable.ic_notification)
//                    .setContentTitle(ite(mData.Topic)
//                    .setContentText("Deadline : " + itemData.Date + " " + itemData.Time)
//                    .setAutoCancel(true)
//                    .setFullScreenIntent(pendingIntent, true)
//                    .setVisibility(Notification.VISIBILITY_PRIVATE)
//                    .setCategory(Notification.CATEGORY_MESSAGE)
//        }
//        else {
//            builder.setSmallIcon(R.drawable.ic_notification)
//                    .setContentTitle(itemData.Topic)
//                    .setContentText("Deadline : " + itemData.Date + " " + itemData.Time)
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent)
//        }



//        Log.d("itit ",it.getBooleanExtra("notification",false).toString())
//        Toast.makeText(context,it.getBooleanExtra("notification",false).toString(),Toast.LENGTH_LONG).show()
        manager.notify(intent.getIntExtra("i", 0), builder.build())
//        if (context is Application) {
//            Toast.makeText(context, "pass", Toast.LENGTH_LONG).show()
//        }

    }

}


