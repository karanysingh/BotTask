package com.example.botcontrol

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*

var listDevices: ArrayList<String> = arrayListOf()
open class bluetooth : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        val listDevicesView: ListView = findViewById<ListView>(R.id.bluetooth_listView)
//        val redColor = Color.parseColor("#FF0000")
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//        listDevicesView.setBackgroundColor(redColor)
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevicesList(pairedDevices,listDevicesView)
        listDevicesView.adapter = MyCustomAdapter(this, listDevices)
    }

    private fun pairedDevicesList(pairedDevices: Set<BluetoothDevice>?,listDevicesView: ListView) {
        if (pairedDevices != null) {
            if (pairedDevices.isNotEmpty()) {
                for (i in pairedDevices) {
                    listDevices.add("${i.name} ${i.address}")
                }
            }
        } else {
            Toast.makeText(this, "No paired Device found", Toast.LENGTH_SHORT).show()
        }
//        val arrayAdapter: ArrayAdapter<String> =
//            ArrayAdapter(this, android.R.layout.simple_list_item_1, listDevices)
//        listDevicesView.adapter = arrayAdapter

        listDevicesView.setOnItemClickListener { parent:AdapterView<*>, v:View, position:Int, id:Long ->
//             Get the device MAC address, the last 17 chars in the View
            if(position == 0){
                Toast.makeText(this,"Clicked on first item",Toast.LENGTH_SHORT).show()
            }
            val info = (v as TextView).text.toString()
            val address = info.substring(info.length - 17)
//             Make an intent to start next activity.
            val i = Intent(this, MainActivity::class.java)
            println("Selected -------> $info $address")
//            Change the activity.
            i.putExtra(
                "EXTRA_ADDRESS",
                address
            )
            i.putExtra(
                "INDEX",
                position.toString()
            )
            finish()
            startActivity(i)
        }
//    private val myListClickListener = { parent, v, position, id ->
//            run {
//                // Get the device MAC address, the last 17 chars in the View
//                val info = (v as TextView).text.toString()
//                val address = info.substring(info.length - 17)
//                // Make an intent to start next activity.
//                val i = Intent(this, MainActivity::class.java)
//                //Change the activity.
//                i.putExtra(
//                    EXTRA_ADDRESS,
//                    address
//                )
//                startActivity(i)
//            }
//        }

    }}
private class MyCustomAdapter(context: Context, items: ArrayList<String>) : BaseAdapter() {
    private val mContext: Context
    private val items: ArrayList<String>

    init {
        this.mContext = context
        this.items = items
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return "TEST STRING"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = TextView(mContext)
        textView.text = items[position]
//        textView.text = "items[positi
        return textView
    }
}