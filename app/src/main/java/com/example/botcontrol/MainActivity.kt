package com.example.botcontrol

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


//import androidx.test.core.app.ApplicationProvider.getApplicationContext


const val REQUEST_ENABLE_BT = 4
private const val TAG = "BOT CONTROL"
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
val socket = null
var address: String = "00:13:EF:00:56:D9"
var btSocket: BluetoothSocket? = null
var isBtConnected = false
var bluetoothAdapter: BluetoothAdapter? = null
var deviceToTry = "1"
var pairedDevices: Set<BluetoothDevice>?= null
var device2Bt: BluetoothDevice? = null
val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
val looper: Looper? = null
//val btHandler = Handler(Looper looper)
open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //question  mark represents that its nullable
        MainActivity.appContext = applicationContext
        val scrollData: ArrayList<String> = arrayListOf("Data\n")
        val dataBox: TextView = findViewById(R.id.textView3)
        val toggleBt: Button = findViewById(R.id.toggle)
        val toggleTransmission: Button = findViewById(R.id.transmission)
        val devicebtn: Button = findViewById(R.id.devices)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var deviceName: String? = null
        var deviceHardwareAddress: String? = null
        val connectBtn: Button = findViewById(R.id.connect)
        toggleBt.setOnClickListener {
            if (bluetoothAdapter == null) {
                Toast.makeText(this,"Device doesn't support Bluetooth or no device found",Toast.LENGTH_SHORT).show()
            }
            if (bluetoothAdapter?.isEnabled == false) {
                val intent = Intent(this, bluetooth::class.java)
                startActivity(intent)
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            if (bluetoothAdapter?.isEnabled == true) {
                bluetoothAdapter!!.disable();
            }
        };
        devicebtn.setOnClickListener {
            if (bluetoothAdapter?.isEnabled == true) {
                val intent = Intent(this, bluetooth::class.java)
                startActivity(intent)
            }
            else if (bluetoothAdapter?.isEnabled == false) {
            Toast.makeText(this,"Enable Bluetooth first!",Toast.LENGTH_SHORT).show()
        }
        }
        connectBtn.setOnClickListener {
            if (bluetoothAdapter?.isEnabled == false) {
                val toast: Toast = Toast.makeText(this, "Enable Bt first!", Toast.LENGTH_SHORT)
                toast.show()
            }
            if (bluetoothAdapter?.isEnabled == true) {
                pairedDevices = bluetoothAdapter?.bondedDevices
                pairedDevices?.forEach { device ->
                    deviceName = device.name
                    deviceHardwareAddress = device.address // MAC address
                }
//            val uuid = UUID.fromString(deviceHardwareAddress)
                val bundle: Bundle? = intent.extras
                deviceToTry = "1"
                try{
                    deviceToTry = intent.getStringExtra("INDEX")
                    address = intent.getStringExtra("EXTRA_ADDRESS")
                    Toast.makeText(this, address, Toast.LENGTH_SHORT).show()
                    scrollData.add("Trying Connecting to $address")
                    dataBox.text = "Trying Connecting to $address"
//                    BluetoothClient(pairedDevices.toList()[ind.toInt()]).start()
                    println("BLuetooth socket: "+ btSocket)
                }
                catch (e: java.lang.Exception){
                    println(e)
                }
                }
                ConnectBT().execute()
            }
        val get:Button = findViewById(R.id.get)
        get.setOnClickListener {
            println("one->"+bluetoothAdapter?.getProfileConnectionState(BluetoothHeadset.HEADSET))
            val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

            scheduler.scheduleAtFixedRate(Runnable {
                println("Runnning")
                val bufferSize = 1024
                val buffer = ByteArray(bufferSize)
                var data = btSocket?.inputStream?.read(buffer)
                scrollData.add(data.toString())
                dataBox.text = scrollData.toString()
            }, 0, 2, TimeUnit.SECONDS)
        }
        toggleTransmission.setOnClickListener {

            if (bluetoothAdapter?.isEnabled == true) {
            println(toggleTransmission.text)
            try{
            if (toggleTransmission.text == "Manual!") {
                toggleTransmission.setText(R.string.ToggleTransmissionAutomatic)
                btSocket?.outputStream!!.write("Q".toByteArray())
            } else {
                toggleTransmission.setText(R.string.ToggleTransmissionManual)
                btSocket?.outputStream!!.write("M".toByteArray())
            }}
            catch(e: java.lang.Exception){
                Toast.makeText(this,"On press toggle : $e",Toast.LENGTH_SHORT).show()
            }}
        }
        if (isBtConnected) {
        keyA.setOnClickListener{
            btSocket?.outputStream!!.write("A".toByteArray())
        }
        keyD.setOnClickListener{
            btSocket?.outputStream!!.write("D".toByteArray())
        }
        keyS.setOnClickListener{
            btSocket?.outputStream!!.write("S".toByteArray())
        }
        keyW.setOnClickListener{
            btSocket?.outputStream!!.write("W".toByteArray())
        }
        }
    }
    companion object {

        lateinit var appContext: Context
        val BUNDLE_KEY = "handlerMsgBundle"
        private val TAG = javaClass.simpleName

    }
    }

private class recieveStream : AsyncTask<Void?, Void?, Void?>() {
    override fun doInBackground(vararg params: Void?): Void? {
        // TODO Auto-generated method stub
        Looper.prepare()
        var mHandler = Handler(){
            val callback = Handler.Callback { message ->
                println(message)
                true
            }
            true
        }
        Looper.loop()
        var bundle = Bundle()
        var message = Message()
        bundle.putString(MainActivity.BUNDLE_KEY,"Hi")
        message.data = bundle
        mHandler.sendMessage(message)
        return null
    }
}

private class ConnectBT: AsyncTask<Void, Void, Void>() {
    private var ConnectSuccess:Boolean = true;
    override fun onPreExecute() {
        msg("Attempting to Connecting")
    }

    override fun doInBackground(vararg params: Void?): Void? {
        println("running in background")
        try {
            if( btSocket == null || isBtConnected){
//                val device = pairedDevices!!.toList()[deviceToTry.toInt()]
                device2Bt = bluetoothAdapter?.getRemoteDevice(address);
                println("deivices"+pairedDevices.toString())
                println("Deviceone"+ device2Bt.toString())
//                println("devicetwo"+device.toString())
                btSocket = device2Bt?.createRfcommSocketToServiceRecord(myUUID)
                btSocket?.connect()
            }
        }catch (e:Exception){
            println("Connection Failed "+e)
            ConnectSuccess = false
    }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        if(!ConnectSuccess){
            msg("Connection Failed try again")
        }else{
            msg("Connection Successful")
            recieveStream().execute()
            isBtConnected = true
        }
    }
    private fun msg(s:String){
        var context = MainActivity.appContext
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }
    private fun Disconnect() {
        if(btSocket!=null){
            try{
                btSocket!!.close()
            }
            catch (e:Exception){
                msg("Error")
            }
        }
    }
}