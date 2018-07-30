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

class ToEdit_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_layout)

        val update = intent.getBooleanExtra("Update", false)
        if(update){ update()}
        listener(update)
    }

    private fun listener(update: Boolean){
        val pref = Preference(this)
        save.setOnClickListener {
            if(!update){
                val i = pref.getLocation()
                val state = false
                saveData(i, pref, state)
            }
            else{
                val DataList = Gson().fromJson(intent.getStringExtra("DataList"), ListData::class.java)
                val i = DataList.Location
                val state = DataList.State
                saveData(i, pref, state)
            }
        }
    }

    private fun update(){
        val DataList = Gson().fromJson(intent.getStringExtra("DataList"), ListData::class.java)
        setNameText.setText(DataList.Topic)
        setDateText.setText(DataList.Date)
        setContentText.setText(DataList.Content)
    }

    private fun saveData(i: Int, pref:Preference, state: Boolean){
        val intent = Intent(this, ToDoList_Activity::class.java)
        val name : String = setNameText.text.toString()
        val deadline: String = setDateText.text.toString()
        val content: String = setContentText.text.toString()
        val listData = ListData(i, name, deadline, content, state)
        val jsonDataString = Gson().toJson(listData)
        pref.setData(jsonDataString, i.toString())
        startActivity(intent)
    }
}