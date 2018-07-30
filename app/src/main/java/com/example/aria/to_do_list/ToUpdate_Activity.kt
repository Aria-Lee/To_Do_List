package com.example.aria.to_do_list

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_to_do_list.*
import kotlinx.android.synthetic.main.edit_layout.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ToUpdate_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_layout)

        val DataList = Gson().fromJson(intent.getStringExtra("DataList"), ListData::class.java)

        setNameText.setText(DataList.Topic)
        setDateText.setText(DataList.Date)
        setContentText.setText(DataList.Content)
        val i = DataList.Location


        save.setOnClickListener {
            val intent = Intent(this, ToDoList_Activity::class.java)
            val pref = Preference(this)
            val name : String = setNameText.text.toString()
            val deadline: String = setDateText.text.toString()
            val content: String = setContentText.text.toString()
            val listData = ListData(i, name, deadline, content, DataList.State)
            val jsonDataString = Gson().toJson(listData)
            Log.d("db Save ",jsonDataString)
            pref.setData(jsonDataString, i.toString())

            startActivity(intent)
        }

    }
}