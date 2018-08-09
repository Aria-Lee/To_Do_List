package com.example.aria.to_do_list

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
import android.widget.TimePicker
import android.widget.Toast
import java.text.SimpleDateFormat


class ToEdit_Activity : AppCompatActivity() {

    lateinit var pref : Preference
    val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.edit_layout)

        pref = Preference(this)

        val update = intent.getBooleanExtra("Update", false)
        if (update) update()
        initListener(update)
    }

    lateinit var DataList: ListData

    private fun initListener(update: Boolean) {
        save.setOnClickListener {
            if (update) {
                val i = DataList.Location
                val state = DataList.State
                saveData(i, state)
            } else {
                val i = pref.getLocation()
                val state = false
                saveData(i, state)
            }
        }

        setTimeText!!.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
               timePick()
            }
            true
        }

        setDateText!!.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action==MotionEvent.ACTION_DOWN) {
                datePick()
            }
            true
        }

    }


    private fun update(){
        DataList = Gson().fromJson(intent.getStringExtra("DataList"), ListData::class.java)
        setNameText.setText(DataList.Topic)
        setDateText.setText(DataList.Date)
        setTimeText.setText(DataList.Time)
        setContentText.setText(DataList.Content)
    }

    private fun saveData(i: Int, state: Boolean){
//        val intent = Intent(this, ToDoList_Activity::class.java)
        val name : String = setNameText.text.toString()
        val date = setDateText.text.toString()
        val time =setTimeText.text.toString()
        val content: String = setContentText.text.toString()
        val itemData = ListData(i, name, date, time, cal.timeInMillis, content, state)
        val jsonDataString = Gson().toJson(itemData)
        pref.setData(jsonDataString, i.toString())
//        Toast.makeText(this, time,Toast.LENGTH_LONG).show()
        alarm(itemData,jsonDataString)
        finish()
    }

    fun timePick(){
        TimePickerDialog(this@ToEdit_Activity,
                timeSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),

                true).show()
    }

    fun datePick(){
        DatePickerDialog(this@ToEdit_Activity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker, min: Int, sec: Int) {
            cal.set(Calendar.HOUR_OF_DAY, min)
            cal.set(Calendar.MINUTE, sec)
            updateTimeInView()
        }
    }

    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                               dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
    }

    private fun updateDateInView() {
        val dateFormat = "yyyy.MM.dd" // mention the format you need
        setDateText!!.setText(SimpleDateFormat(dateFormat, Locale.US).format(cal.time))
    }

    private fun updateTimeInView(){
        val timeFormat = "HH:mm:ss"
        setTimeText!!.setText(SimpleDateFormat(timeFormat, Locale.US).format(cal.time))
    }

    private fun alarm(listData:ListData, jsonDataString:String) {
        val am =getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("itemData", jsonDataString)
//        intent.putExtra("Topic", listData.Topic)
//        intent.putExtra("Deadline", listData.Date +" "+ listData.Time)
        intent.putExtra("i", listData.Location)
        val pending = PendingIntent.getBroadcast(this.applicationContext, listData.Location, intent, PendingIntent.FLAG_ONE_SHOT)
        val Info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager.AlarmClockInfo(System.currentTimeMillis(),pending)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setAlarmClock(Info,pending)

//          am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pending)
        }
        else am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pending)
    }
}
