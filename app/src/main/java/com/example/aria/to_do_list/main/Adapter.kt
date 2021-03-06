package com.example.aria.to_do_list.main

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.example.aria.to_do_list.R
import com.example.aria.to_do_list.data.ListData

class Adapter(private var datalist: MutableList<ListData>): RecyclerView.Adapter<Adapter.ViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null
    lateinit var removeItemListener:(Int)-> Boolean


    //提供給外部的setOnItemClickListener方法
    fun setOnItemClickListener(listener: OnItemClickListener) {

        mOnItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.check_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = this.datalist.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position])

        holder.itemView.setOnClickListener {
            mOnItemClickListener!!.onItemClick(datalist[holder.adapterPosition])
        }

        holder.checkBox.setOnClickListener {
            (it as CheckBox).isChecked = !datalist[holder.adapterPosition].State
            mOnItemClickListener!!.checkedClick(datalist[holder.adapterPosition])
        }

    }

    fun clearCheckedItem(){
        var size = (datalist.size - 1)
        var i = 0
        while (i <= size) {
            if (datalist[i].State == true && removeItemListener.invoke(datalist[i].Location)) {
                datalist.removeAt(i)
                size--
                notifyItemRemoved(i)
                notifyItemRangeChanged(i, itemCount - 1)
                i--
            }
            i++
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checkedTextView = itemView.findViewById<TextView>(R.id.checkedTextView)
        val checkDate = itemView.findViewById<TextView>(R.id.checkDate)
        val checkBox= itemView.findViewById<CheckBox>(R.id.checkBox)

        fun bind(itemData: ListData) {
            val deadLine = itemData.Deadline.split("  ")
            val checkDateText = deadLine[0]+"\n"+deadLine[1]
            checkedTextView.text = itemData.Topic
            checkDate.text = checkDateText
            checkBox.isChecked = itemData.State
        }
    }

    //設置可供外部使用的OnItemClickListener接口
    interface OnItemClickListener {
        //position是用來處理每個位子的資料設定，並非判斷目前點擊的項目為何
        //點擊項目時，holder即為當時點擊項目的holder
        fun onItemClick(itemData: ListData)
        fun checkedClick(itemData: ListData)
        //因為Acitivity必須透過holder取得checkedTextView，所以需要Viewolder參數
        //點擊項目時，就會從該項目的holder中去取得checkedTextView
        //fun onItemCheck(view: View, viewholder: ViewHolder)
    }

    fun new(newdatalist: MutableList<ListData>){
        if (newdatalist != datalist){
            datalist = newdatalist
            this@Adapter.notifyDataSetChanged()
        }
    }

    fun isDataExit(itemData: ListData):Boolean{
        return datalist.find { i ->
            i.SaveTime==itemData.SaveTime
            } !=null

//            return datalist.contains(itemData)
    }

}





