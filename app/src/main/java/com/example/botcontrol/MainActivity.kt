package com.example.botcontrol

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

const val REQUEST_ENABLE_BT = 4
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //question  mark represents that its nullable
        print("Aagya")
        val toggleBt: Button = findViewById(R.id.toggle)
        val toggleTransmission: Button = findViewById(R.id.transmission)
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        toggleBt.setOnClickListener{
            if (bluetoothAdapter == null){
                print("Device doesn't support Bluetooth or no device found")
            }
            if(bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
            if(bluetoothAdapter?.isEnabled == true){
                bluetoothAdapter.disable();
            }
        };
        toggleTransmission.setOnClickListener {
            println(toggleTransmission.text)
            if(toggleTransmission.text == "Manual!"){
                toggleTransmission.setText(R.string.ToggleTransmissionAutomatic)
            }
            else{
                toggleTransmission.setText(R.string.ToggleTransmissionManual)
            }
        }
    }
}