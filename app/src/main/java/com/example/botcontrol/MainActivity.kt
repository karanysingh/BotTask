package com.example.botcontrol

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.collections.ArrayList

//import androidx.test.core.app.ApplicationProvider.getApplicationContext


const val REQUEST_ENABLE_BT = 4
private const val TAG = "BOT CONTROL"
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

//val btHandler = Handler(Looper looper)
open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //question  mark represents that its nullable

        val scrollData: ArrayList<String> = arrayListOf("Data\n")
        val dataBox: TextView = findViewById(R.id.textView3)
        val toggleBt: Button = findViewById(R.id.toggle)
        val toggleTransmission: Button = findViewById(R.id.transmission)
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        var deviceName: String? = null
        var deviceHardwareAddress: String? = null
        val connectBtn: Button = findViewById(R.id.connect)
//        val btStream: MyBluetoothService = MyBluetoothService()
        toggleBt.setOnClickListener {
            if (bluetoothAdapter == null) {
                println("Device doesn't support Bluetooth or no device found")
            }
            if (bluetoothAdapter?.isEnabled == false) {
                val intent = Intent(this, bluetooth::class.java)
                startActivity(intent)
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            if (bluetoothAdapter?.isEnabled == true) {
                bluetoothAdapter.disable();
            }
        };
        connectBtn.setOnClickListener {
            if (bluetoothAdapter?.isEnabled == false) {
                val toast: Toast = Toast.makeText(this, "Enable Bt first!", Toast.LENGTH_SHORT)
                toast.show()
            }
            if (bluetoothAdapter?.isEnabled == true) {
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                pairedDevices?.forEach { device ->
                    deviceName = device.name
                    deviceHardwareAddress = device.address // MAC address
                }
//            val uuid = UUID.fromString(deviceHardwareAddress)
                scrollData.add(pairedDevices.toString())
                dataBox.text = scrollData.toString()
                println("These are paired already $deviceHardwareAddress")
                val device = pairedDevices!!.first()
                BluetoothClient(device).start()
            }
        }
        toggleTransmission.setOnClickListener {
            println(toggleTransmission.text)
            if (toggleTransmission.text == "Manual!") {
                toggleTransmission.setText(R.string.ToggleTransmissionAutomatic)
            } else {
                toggleTransmission.setText(R.string.ToggleTransmissionManual)
            }
        }
//        bluetoothAdapter?.startDiscovery()
    }

}

class BluetoothClient(device: BluetoothDevice) : Thread() {
    val uuid: UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")
    private val socket = device.createRfcommSocketToServiceRecord(uuid)
    override fun run() {
        Log.i("Client", "Connecting")
        this.socket.connect()

        Log.i("Client", "Sending")
        val outputStream = this.socket.outputStream
        val inputStream = this.socket.inputStream
        try {
            var message = "Hi"
            outputStream.write(message.toByteArray())
            outputStream.flush()
            Log.i("Client", "Sent")

        } catch (e: Exception) {
            Log.e("client", "cannot send", e)
        } finally {
            outputStream.close()
            inputStream.close()
            this.socket.close()
        }
    }
}