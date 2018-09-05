package com.example.aria.to_do_list.main

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.example.aria.to_do_list.AlarmReceiver
import com.example.aria.to_do_list.R
import com.example.aria.to_do_list.data.CloudFirestore
import com.example.aria.to_do_list.data.ListData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_to_do_list.*
import kotlinx.android.synthetic.main.dialog_title.view.*
import kotlinx.android.synthetic.main.show_event.view.*
import android.net.NetworkInfo
import android.net.ConnectivityManager
import com.example.aria.to_do_list.ClearAlarm


class ToDoList_Activity : AppCompatActivity() {

    lateinit var cf: CloudFirestore
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

        val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo == null) networkStateDialog("網路連線未開啟")
        else if (!mNetworkInfo.isAvailable()) networkStateDialog("網路連線異常")

        cf = CloudFirestore(this)
        getAll()
        recyclerview.adapter = initAdapter()
        recyclerview.layoutManager = LinearLayoutManager(this)
        checkAll.setOnClickListener { chooseAll() }
    }

    lateinit var adapter: Adapter

    private fun initAdapter(): Adapter {
        adapter = Adapter(list)
        val clearAlarm=ClearAlarm()

        adapter.setOnItemClickListener(object : Adapter.OnItemClickListener {
            override fun checkedClick(itemData: ListData) {
                itemData.state = !itemData.state
                cf.updateData(itemData.key, "state", itemData.state)

            }

            override fun onItemClick(itemData: ListData) {
                showListItemDialog(itemData)
            }
        })



        adapter.removeItemListener = {
            key: Long ->
            cf.deleteData(key) && clearAlarm.cancelAlarm(this,key)
        }

        return adapter
    }

    private fun networkStateDialog(string: String) {
        AlertDialog.Builder(this@ToDoList_Activity)
                .setTitle("提醒")
                .setMessage(string + "\n資料將不會同步備份至雲端\n\n*將在連線正常時同步至雲端")
                .setPositiveButton("OK") { dialog, which ->
                    dialog.cancel()
                }
                .create()
                .show()
    }

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
        cf.getAll { it ->
            it.sortWith(compareBy({ it.deadline }, { it.topic }))
            list = it
            adapter.new(list)
            notification()
        }
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

    private fun intentFromNotification(intent: Intent?) {
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

    private fun notification() {
        intentFromNotification(intent)
        intent = null
    }

    private fun chooseAll() {
        checkAll.isChecked = checkAll.isChecked
        for (i in 0..list.size - 1) {
            list[i].state = checkAll.isChecked
            cf.updateData(list[i].key, "state", checkAll.isChecked)
            (recyclerview.adapter as Adapter).notifyDataSetChanged()
        }
    }
}




