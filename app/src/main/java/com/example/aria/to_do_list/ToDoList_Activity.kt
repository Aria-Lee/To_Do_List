package com.example.aria.to_do_list

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_to_do_list.*
import kotlinx.android.synthetic.main.show_event.view.*

class ToDoList_Activity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_to_do_list)
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.to_do_list_title);

        val pref = Preference(this)

        recyclerview.adapter = initAdapter(pref,loadPersistData(pref))
        recyclerview.layoutManager = LinearLayoutManager(this)

        initListener()
    }

    private fun initAdapter(pref:Preference, persistData: MutableList<ListData>): Adapter {
        val adapter = Adapter(persistData)

        adapter.setOnItemClickListener(object : Adapter.OnItemClickListener{
            override fun checkedClick(itemData: ListData) {
                if (!itemData.State) {
                    itemData.State = true
                    pref.setData(Gson().toJson(itemData), itemData.Location.toString())
                } else {
                    itemData.State = false
                    pref.setData(Gson().toJson(itemData), itemData.Location.toString())
                }
            }
            override fun onItemClick(itemData: ListData) { showListItemDialog(itemData) }
            //必須透過viewHolder取得checkedTextView才是recyclerView的checkedTextView
            //直接使用checkedTextView會是獨立讀取layout中的checkedTextView

        })

        adapter.removeItemListener = {loc: Int -> pref.deleteData(loc.toString())}

        Clear.setOnClickListener { adapter.clearCheckedItem() }

        return adapter
    }

//    interface xxx {
//        fun invoke(loc:Int) : Boolean
//    }

    private fun showListItemDialog(itemData: ListData) {
        var view = layoutInflater.inflate(R.layout.show_event, null)
        view.showDate.text = itemData.Date
        view.showContent.text = itemData.Content

        //
        AlertDialog.Builder(this@ToDoList_Activity)
                .setView(view)
                .setTitle(itemData.Topic)
                .setPositiveButton("OK") { dialog, which ->
                    val intent = Intent(this@ToDoList_Activity, ToDoList_Activity::class.java)
                    startActivity(intent)
                }
                .setNeutralButton("Edit") { dialog, which ->
                    val intent = Intent(this@ToDoList_Activity, ToEdit_Activity::class.java)
                    val dataString = Gson().toJson(itemData)
                    intent.putExtra("DataList", dataString)
                    intent.putExtra("Update", true)
                    startActivity(intent)
                }
                .create()
                .show()
    }

    private fun loadPersistData(pref:Preference): MutableList<ListData> {
        val list = mutableListOf<ListData>()
        for (key in pref.getAll(this)!!.keys) {
            val data = Gson().fromJson(pref.getData(key), ListData::class.java)
                list.add(data)
        }
        return list
    }

    private fun initListener() {
        Add.setOnClickListener { addItemClickListener.invoke() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete){
            Toast.makeText(this,"Delete!!!",Toast.LENGTH_SHORT).show()
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    val addItemClickListener = {
        val intent = Intent(this, ToEdit_Activity::class.java)
        intent.putExtra("Update", false)
        startActivity(intent)
    }


}


