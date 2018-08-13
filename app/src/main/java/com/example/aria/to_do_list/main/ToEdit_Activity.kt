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
import android.view.MotionEvent
import kotlinx.android.synthetic.main.edit_layout.*
import com.google.gson.Gson
import java.util.*
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.example.aria.to_do_list.AlarmReceiver
import com.example.aria.to_do_list.R
import com.example.aria.to_do_list.data.ListData
import com.example.aria.to_do_list.data.Preference
import java.text.SimpleDateFormat


class ToEdit_Activity : AppCompatActivity() {

    lateinit var pref : Preference
    val deadlineCal = Calendar.getInstance()
    val notiCal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.edit_layout)

        pref = Preference(this)

        val update = intent.getBooleanExtra("Update", false)
        if (update) update()
        initListener(update)
    }

    lateinit var itemData: ListData

    private fun initListener(update: Boolean) {
        save.setOnClickListener {
            if (update) {
                val i = itemData.Location
                val state = itemData.State
//                cancelAlarm(itemData)
                saveData(i, state)
            } else {
                val i = pref.getLocation()
                val state = false
                saveData(i, state)
            }
        }

        setDateText!!.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action==MotionEvent.ACTION_DOWN) {
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



        setNotiDateText!!.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action==MotionEvent.ACTION_DOWN) {
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


    private fun update(){
        itemData = Gson().fromJson(intent.getStringExtra("itemData"), ListData::class.java)
        val deadline = itemData.Deadline.split("  ")
        val notiTime = itemData.NotiTime.split("  ")
        setNameText.setText(itemData.Topic)
        setDateText.setText(deadline[0])
        setTimeText.setText(deadline[1])
        setNotiDateText.setText(notiTime[0])
        setNotiTimeText.setText(notiTime[1])
//        setDateText.setText(itemData.Deadline)
//        setTimeText.setText(itemData.NotiTime)
        setContentText.setText(itemData.Content)
    }

    private fun saveData(i: Int, state: Boolean){
//        val intent = Intent(this, ToDoList_Activity::class.java)
        val name : String = setNameText.text.toString()
        val deadline = setDateText.text.toString() + "  " + setTimeText.text.toString()
        val notiTime =setNotiDateText.text.toString() + "  " +setNotiTimeText.text.toString()
        val content: String = setContentText.text.toString()
        val saveTime = System.currentTimeMillis()
        val itemData = ListData(i, name, deadline, notiTime, notiCal.timeInMillis, content, state, saveTime)
        val jsonDataString = Gson().toJson(itemData)
        pref.setData(jsonDataString, i.toString())
        alarm(itemData,jsonDataString)
        finish()
    }

    fun timePick(cal : Calendar, textView: TextView){
        TimePickerDialog(this@ToEdit_Activity,
                timeSetListener(cal,textView),
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true).show()
    }

    fun datePick(cal : Calendar, textView: TextView){
        DatePickerDialog(this@ToEdit_Activity,
                dateSetListener(cal, textView),
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun  timeSetListener(cal: Calendar,textView : TextView) = object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker, min: Int, sec: Int) {
            cal.set(Calendar.HOUR_OF_DAY, min)
            cal.set(Calendar.MINUTE, sec)
            cal.set(Calendar.SECOND,0)
            updateTimeInView(cal, textView)
        }
    }

    fun dateSetListener(cal: Calendar,textView : TextView) = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                               dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(cal, textView)
        }
    }

    private fun updateDateInView(cal: Calendar ,textView : TextView) {
        val dateFormat = "yyyy.MM.dd" // mention the format you need
        textView.text = (SimpleDateFormat(dateFormat, Locale.US).format(cal.time))
    }

    private fun updateTimeInView(cal: Calendar, textView : TextView){
        val timeFormat = "HH:mm"
        textView.text = SimpleDateFormat(timeFormat, Locale.US).format(cal.time)
    }

    private fun alarm(itemData: ListData, jsonDataString:String) {
        val am =getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("itemData", jsonDataString)
        intent.putExtra("i", itemData.Location)

        val pending = PendingIntent.getBroadcast(this.applicationContext, itemData.Location, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, itemData.NotifyTime, pending)
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pending)
        }
        else
            am.setExact(AlarmManager.RTC_WAKEUP, itemData.NotifyTime, pending)
//            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pending)
    }

}
