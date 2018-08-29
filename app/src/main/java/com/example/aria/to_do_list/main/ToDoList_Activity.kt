package com.example.aria.to_do_list.main

import android.app.*
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.example.aria.to_do_list.AlarmReceiver
import com.example.aria.to_do_list.R
//import com.example.aria.to_do_list.data.ListData
import com.example.aria.to_do_list.data.Room.ListData
import com.example.aria.to_do_list.data.Room.ToDoDao
import com.example.aria.to_do_list.data.Room.ToDoDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_to_do_list.*
import kotlinx.android.synthetic.main.dialog_title.view.*
import kotlinx.android.synthetic.main.show_event.view.*


class ToDoList_Activity : AppCompatActivity() {
    lateinit var dbDao : ToDoDao
    var list = mutableListOf<ListData>()


    override fun onRestart() {
        super.onRestart()
        getAll()
        checkAll.isChecked = false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentFromNotification(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)
        dbDao = ToDoDatabase.getInstance(this@ToDoList_Activity)!!.ToDoDao()
        getAll()
        recyclerview.adapter = initAdapter()
        recyclerview.layoutManager = LinearLayoutManager(this)
        checkAll.setOnClickListener { chooseAll() }
    }

    lateinit var adapter: Adapter

    private fun initAdapter(): Adapter {
        adapter = Adapter(list)

        adapter.setOnItemClickListener(object : Adapter.OnItemClickListener {
            override fun checkedClick(itemData: ListData) {

                itemData.state = !itemData.state
                Thread(Runnable {
                    dbDao.update(itemData)
                }).start()
            }

            override fun onItemClick(itemData: ListData) {
                showListItemDialog(itemData)
            }
            //必須透過viewHolder取得checkedTextView才是recyclerView的checkedTextView
            //直接使用checkedTextView會是獨立讀取layout中的checkedTextView
        })



        adapter.removeItemListener = {
            itemData: ListData -> delete(itemData)  && cancelAlarm(itemData.key)
        }

        return adapter
    }

//    interface xxx {
//        fun invoke(loc:Int) : Boolean
//    }

    private fun showListItemDialog(itemData: ListData) {
        val view = layoutInflater.inflate(R.layout.show_event, null)
        view.showDate.text = itemData.deadline
        view.showNotiTime.text = itemData.notiTime
        view.showContent.text = itemData.content
        val titleView = layoutInflater.inflate(R.layout.dialog_title, null)
        titleView.dialogTitle.text = itemData.topic

        AlertDialog.Builder(this@ToDoList_Activity)
                .setView(view)
                .setCustomTitle(titleView)
                .setPositiveButton("OK") { dialog, which ->
                    dialog.cancel()
                }
                .setNeutralButton("Edit") { dialog, which ->
                    val intent = Intent(this@ToDoList_Activity, ToEdit_Activity::class.java)
                    val dataString = Gson().toJson(itemData)
                    intent.putExtra("itemData", dataString)
                    intent.putExtra("Update", true)
                    startActivity(intent)
                }
                .create()
                .show()
    }


    private fun getAll() {
        Thread(Runnable {
            var data = dbDao.getAll()
            list = data.sortedWith(compareBy({ it.deadline }, { it.topic })).toMutableList()
            this@ToDoList_Activity.runOnUiThread(Runnable {
                adapter.new(list)
                notification()
            })
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

//    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            Toast.makeText(this, "Delete!!!", Toast.LENGTH_SHORT).show()
            adapter.clearCheckedItem()
            checkAll.isChecked = false
            //或不在外面宣告adapter(僅在initAdpater中宣告)
            //並在這邊直接 recyclerview.adapter as Adpater
            return true
        }
        if (item.itemId == R.id.menu_add) {
            addItemClickListener.invoke()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    val addItemClickListener = {
        val intent = Intent(this, ToEdit_Activity::class.java)
        intent.putExtra("Update", false)
        startActivity(intent)
    }

    fun intentFromNotification(intent: Intent?) {
        intent?.let {
            if (it.getBooleanExtra("notification", false)) {
                val itemData = Gson().fromJson(it.getStringExtra("itemData"), ListData::class.java)
                if ((recyclerview.adapter as Adapter).isDataExit(itemData)) {
                    showListItemDialog(itemData)
                } else {
                    Toast.makeText(this, "The event doesn't exist.", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    fun notification() {
        intentFromNotification(intent)
        intent = null
    }


    fun chooseAll() {
        checkAll.isChecked = checkAll.isChecked
        for (i in 0..list.size - 1) {
            list[i].state = checkAll.isChecked
            Thread(Runnable { dbDao.update(list[i]) }).start()
//            cf.updateData(list[i].key, "state", checkAll.isChecked)
            (recyclerview.adapter as Adapter).notifyDataSetChanged()
        }
    }

    fun delete(itemData: ListData):Boolean{
        Thread(Runnable {
            dbDao.delete(itemData)
        }).start()
        return true
    }

    private fun cancelAlarm(key: Long): Boolean {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this.applicationContext, (key % Int.MAX_VALUE).toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
        return true

    }
}




