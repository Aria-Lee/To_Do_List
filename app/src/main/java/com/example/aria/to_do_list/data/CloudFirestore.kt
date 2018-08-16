package com.example.aria.to_do_list.data

import android.app.Application
import android.content.Context
import android.nfc.Tag
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.Exception
import java.util.*
import kotlin.coroutines.experimental.coroutineContext




class CloudFirestore (var context: Context){

    val fb = FirebaseFirestore.getInstance()
    val eventData = fb.collection("ToDoList")

    fun setData(string: String, key:String) {
        eventData.document(key)
                .set(string)
                .addOnCompleteListener{task->
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Event added", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                        }
                    }
    }

    fun updateData(key:String, content:Any){
        eventData.document(key).update(key, content)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(context, "Event updated", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(context, "Update Failed", Toast.LENGTH_LONG).show()

                    }
                }

    }

    fun getData(key: String):String?{
        var document :String? = null
                eventData.document(key).get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Log.d("tag", "Success", task.getException())
                        document = eventData.document("key").get().toString()
                    }
                    else {
                        Log.d("tag", "Fail", task.getException())
                    }
                }
        return document
    }

    fun getAll(context: Context): MutableMap<String, *>? {
        var map :MutableMap<String, *>? = null
        eventData.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    Log.d("TAG", document.id + " => " + document.data)
                    map = document.data
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.exception)
            }
        }
        return map
    }
//
//    fun getLocation(): Int {
//        var i = 0
//        while (i >= 0) {
//            eventData.document(i.toString()).get().addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    break
//                } else {
//                    i++
//                }
//            }
//        }
////            if (eventData.document(i.toString()).) {
////                break
////            }
////            i++
////        }
//        return i
//    }

    fun deleteData(key:String):Boolean {
        eventData.document(key).delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("tag", "Success", task.getException())
                    } else {
                        Log.d("tag", "Fail", task.getException())
                    }
                }
        return true
    }
}


