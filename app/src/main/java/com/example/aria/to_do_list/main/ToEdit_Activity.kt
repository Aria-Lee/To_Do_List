package com.example.aria.to_do_list.main

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import kotlinx.android.synthetic.main.edit_layout.*
import com.google.gson.Gson
import java.util.*
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.example.aria.to_do_list.AlarmReceiver
import com.example.aria.to_do_list.R
import com.example.aria.to_do_list.data.Preference
import com.example.aria.to_do_list.data.Room.ListData
import com.example.aria.to_do_list.data.Room.ToDoDatabase
import java.text.SimpleDateFormat


class ToEdit_Activity : AppCompatActivity() {

    val dbDao = ToDoDatabase.getInstance(this@ToEdit_Activity)!!.ToDoDao()
    val deadlineCal = Calendar.getInstance()
    val notiCal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_layout)

        val update = intent.getBooleanExtra("Update", false)
        if (update) update()
        initListener(update)
    }

    lateinit var orgItemData: ListData

    private fun initListener(update: Boolean) {
        save.setOnClickListener {
            val itemData = getData(update)
            saveData(itemData, update)
        }


        setDateText.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                datePick(deadlineCal, setDateText)
            }
            true
        }

        setTimeText!!.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                timePick(deadlineCal, setTimeText)
            }
            true
        }


        setNotiDateText.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                datePick(notiCal, setNotiDateText)
            }
            true
        }

        setNotiTimeText.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                timePick(notiCal, setNotiTimeText)
            }
            true
        }
    }


    private fun update() {
        orgItemData = Gson().fromJson(intent.getStringExtra("itemData"), ListData::class.java)
        val deadline = orgItemData.deadline.split("  ")
        val notiTime = orgItemData.notiTime.split("  ")
        val sdf = SimpleDateFormat("yyyy.MM.dd  HH:mm")
        val initDeadline = sdf.parse(orgItemData.deadline)
        val initNotiTime = sdf.parse(orgItemData.notiTime)
        deadlineCal.setTime(initDeadline)
        notiCal.setTime(initNotiTime)
        setNameText.setText(orgItemData.topic)
        setDateText.setText(deadline[0])
        setTimeText.setText(deadline[1])
        setNotiDateText.setText(notiTime[0])
        setNotiTimeText.setText(notiTime[1])
        setContentText.setText(orgItemData.content)
    }

    //    private fun saveData(i: Int, state: Boolean) {
    private fun getData(update: Boolean): ListData {
        val state : Boolean
        val itemData :ListData
        val topic: String = setNameText.text.toString()
        val deadline = setDateText.text.toString() + "  " + setTimeText.text.toString()
        val notiTime = setNotiDateText.text.toString() + "  " + setNotiTimeText.text.toString()
        val notiMillis = notiCal.timeInMillis
        val content: String = setContentText.text.toString()
        if (update) {
            state = orgItemData.state
            val key = orgItemData.key
            itemData = ListData(key, topic, deadline, notiTime, notiMillis, content, state)
        } else {
            state = false
            itemData = ListData(topic, deadline, notiTime, notiMillis, content, state)
        }
        return itemData
    }

    private fun saveData(itemData: ListData, update: Boolean) {
        Thread(Runnable {
            if (update) {
                dbDao.update(itemData)
                alarm(itemData)
            } else {
                dbDao.insert(itemData)
                val itemData = dbDao.getData()
                alarm(itemData)
            }
        }).start()
        finish()
    }

    fun timePick(cal: Calendar, textView: TextView) {
        TimePickerDialog(this@ToEdit_Activity,
                timeSetListener(cal, textView),
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true).show()
    }

    fun datePick(cal: Calendar, textView: TextView) {
        DatePickerDialog(this@ToEdit_Activity,
                dateSetListener(cal, textView),
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun timeSetListener(cal: Calendar, textView: TextView) = object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker, min: Int, sec: Int) {
            cal.set(Calendar.HOUR_OF_DAY, min)
            cal.set(Calendar.MINUTE, sec)
            cal.set(Calendar.SECOND, 0)
            updateTimeInView(cal, textView)
        }
    }

    fun dateSetListener(cal: Calendar, textView: TextView) = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                               dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(cal, textView)
        }
    }

    private fun updateDateInView(cal: Calendar, textView: TextView) {
        val dateFormat = "yyyy.MM.dd" // mention the format you need
        textView.text = (SimpleDateFormat(dateFormat, Locale.US).format(cal.time))
    }

    private fun updateTimeInView(cal: Calendar, textView: TextView) {
        val timeFormat = "HH:mm"
        textView.text = SimpleDateFormat(timeFormat, Locale.US).format(cal.time)
    }

    private fun alarm(itemData: ListData) {
        val jsonDataString = Gson().toJson(itemData)
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val i = (itemData.key % Int.MAX_VALUE).toInt()
        intent.putExtra("itemData", jsonDataString)
//        intent.putExtra("i", itemData.location)
        intent.putExtra("i", i)

//        val pending = PendingIntent.getBroadcast(this.applicationContext, itemData.location, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pending = PendingIntent.getBroadcast(this.applicationContext, i, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, itemData.notiMillis, pending)
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pending)
        } else
            am.setExact(AlarmManager.RTC_WAKEUP, itemData.notiMillis, pending)
//            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pending)
    }

}
