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


class CloudFirestore(var context: Context) {

    val fb = FirebaseFirestore.getInstance()
    val eventData = fb.collection("ToDoList")

    fun setData(key: Long, map: MutableMap<String, Any?>) {

        eventData.document(key.toString())
                .set(map)
                .addOnSuccessListener {
                    Toast.makeText(context, "Event added", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                }
    }

    fun updateData(key: Long, label: String, content: Any?) {

        eventData.document(key.toString()).update(label, content)
                .addOnSuccessListener {
                    Log.d("TAG", "Event updated")
                }
                .addOnFailureListener {
                    Log.d("TAG", "Failed")
                }
    }

    fun getAll(callback: (MutableList<ListData>) -> Unit) {
        var dataList = mutableListOf<ListData>()
        eventData.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            dataList.add(document.toObject(ListData::class.java))
                        }
                        callback(dataList)
                    } else {
                        Log.d("TAG", "Error", task.exception)
                    }
                }
    }

    fun deleteData(key: Long): Boolean {
        eventData.document(key.toString()).delete()
                .addOnSuccessListener {
                    Log.d("tag", "Success")
                }
                .addOnFailureListener {
                    Log.d("tag", "Fail")
                }
        return true
    }
}


