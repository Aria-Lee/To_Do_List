package com.example.aria.to_do_list

import android.app.*
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_to_do_list.*
import kotlinx.android.synthetic.main.dialog_title.view.*
import kotlinx.android.synthetic.main.show_event.view.*


class ToDoList_Activity : AppCompatActivity() {
    lateinit var pref: Preference
//    lateinit var isDataExist:  (ListData) -> Boolean



    override fun onRestart() {
        super.onRestart()

        (recyclerview.adapter as Adapter).new(loadPersistData(pref))
//        Toast.makeText(this, "HIHI", Toast.LENGTH_LONG).show()
        checkAll.isChecked=false
//        notification()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentFromNotification(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)

        pref = Preference(this)

        recyclerview.adapter = initAdapter(pref, loadPersistData(pref))
        recyclerview.layoutManager = LinearLayoutManager(this)
        notification()
        checkAll.setOnClickListener{chooseAll(loadPersistData(pref))}
    }

    lateinit var adapter: Adapter
    private fun initAdapter(pref: Preference, persistData: MutableList<ListData>): Adapter {
        adapter = Adapter(persistData)

        adapter.setOnItemClickListener(object : Adapter.OnItemClickListener {
            override fun checkedClick(itemData: ListData) {
                if (!itemData.State) {
                    itemData.State = true
                    pref.setData(Gson().toJson(itemData), itemData.Location.toString())
                } else {
                    itemData.State = false
                    pref.setData(Gson().toJson(itemData), itemData.Location.toString())
                }
            }

            override fun onItemClick(itemData: ListData) {
                showListItemDialog(itemData)
            }
            //必須透過viewHolder取得checkedTextView才是recyclerView的checkedTextView
            //直接使用checkedTextView會是獨立讀取layout中的checkedTextView

        })

        adapter.removeItemListener = {
           loc: Int -> pref.deleteData(loc.toString()) && cancelAlarm(loc)
        }

        return adapter
    }

//    interface xxx {
//        fun invoke(loc:Int) : Boolean
//    }

    private fun showListItemDialog(itemData: ListData) {
        val view = layoutInflater.inflate(R.layout.show_event, null)
//        val deadLine = itemData.Deadline.split("  ")
//        val showDate = deadLine[0]+"\n"+deadLine[1]

//        view.showDate.text = itemData.Deadline + "  " + itemData.NotiTime
        view.showDate.text = itemData.Deadline
        view.showNotiTime.text = itemData.NotiTime
        view.showContent.text = itemData.Content
        val titleView = layoutInflater.inflate(R.layout.dialog_title, null)
        titleView.dialogTitle.text = itemData.Topic

        //
        AlertDialog.Builder(this@ToDoList_Activity)
                .setView(view)
                .setCustomTitle(titleView)
                .setPositiveButton("OK") { dialog, which ->
                    //                    val intent = Intent(this@ToDoList_Activity, ToDoList_Activity::class.java)
//                    startActivity(intent)
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

//    var list= mutableListOf<ListData>()
    private fun loadPersistData(pref: Preference): MutableList<ListData> {
        val list = mutableListOf<ListData>()
        for (key in pref.getAll(this)!!.keys) {
            val data = Gson().fromJson(pref.getData(key), ListData::class.java)
            list.add(data)
        }
        list.sortWith(compareBy({ it.Deadline }, { it.Topic }))
        return list
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

    fun intentFromNotification(intent:Intent?){
        intent?.let {
        if (it.getBooleanExtra("notification", false)) {
//            Toast.makeText(this, "fmkslg';D", Toast.LENGTH_LONG).show()
            val itemData = Gson().fromJson(it.getStringExtra("itemData"), ListData::class.java)
            if((recyclerview.adapter as Adapter).isDataExit(itemData)){
                showListItemDialog(itemData)
            }
            else{
                Toast.makeText(this,"The event doesn't exist.",Toast.LENGTH_LONG).show()
            }

        }
        }
    }

    fun notification() {
        intentFromNotification(intent)
            intent = null
    }

    fun chooseAll(list:MutableList<ListData>){
        if (checkAll.isChecked){
            checkAll.isChecked = true
            for (i in 0 .. list.size-1){
            list[i].State = true
            pref.setData(Gson().toJson(list[i]), list[i].Location.toString())
            }
            (recyclerview.adapter as Adapter).new(list)
        }
        else{
            checkAll.isChecked = false
            for (i in 0 .. list.size-1){
                list[i].State = false
                pref.setData(Gson().toJson(list[i]), list[i].Location.toString())
            }
            (recyclerview.adapter as Adapter).new(list)
        }
    }

    private fun cancelAlarm(loc: Int):Boolean{
        val intent = Intent(this, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this.applicationContext, loc, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am =getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
        return true
    }

}


