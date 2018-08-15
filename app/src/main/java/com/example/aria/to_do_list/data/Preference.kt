package com.example.aria.to_do_list.data

import android.content.Context
import com.google.gson.Gson

class Preference (context: Context) {

    private val pref = context.getSharedPreferences("List", Context.MODE_PRIVATE)

    fun setData(string: String, key:Int) {

            pref.edit().putString(key.toString(), string).apply()
    }

    fun getData(key: String):String{
        return  pref.getString(key, null)
    }

    fun getAll(): MutableMap<String, *>? {
//        val sp = context.getSharedPreferences("List",
//                Context.MODE_PRIVATE)
        return pref.getAll()
    }

    fun getLocation(): Int {
        var i = 0
        while (i >= 0) {
            if (pref.contains(i.toString()) == false) {
                break
            }
            i++
        }
        return i
    }

    fun deleteData(key:String):Boolean{
        if(pref.contains(key)){
            pref.edit().remove(key).apply()
            return true
        }
        return false
    }
}
