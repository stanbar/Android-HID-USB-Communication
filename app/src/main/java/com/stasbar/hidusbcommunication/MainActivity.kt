package com.stasbar.hidusbcommunication

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, p1: Intent?) {
            showAllDevices()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reginsterReveiver()

    }

    fun reginsterReveiver() {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val mPremissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(mUsbReceiver, filter)

        manager.deviceList.forEach { s, usbDevice ->
            manager.requestPermission(usbDevice, mPremissionIntent)
        }

    }

    fun showAllDevices() {
        Handler(Looper.getMainLooper()).post({
            val mManager = getSystemService(Context.USB_SERVICE) as UsbManager
            val deviceList: HashMap<String, UsbDevice> = mManager.deviceList
            val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
            val textBuffer = StringBuffer()

            while (deviceIterator.hasNext()) {
                val device = deviceIterator.next()
                textBuffer.append("Manufacturer: " + device.manufacturerName + "\n")
                textBuffer.append("Product: " + device.productName + "\n")
                textBuffer.append("DeviceName: " + device.deviceName + "\n")
                textBuffer.append("ID: " + device.deviceId + "\n")
                textBuffer.append("Class: " + device.deviceClass + "\n")
                textBuffer.append("Protocol: " + device.deviceProtocol + "\n")
                textBuffer.append("Vendor ID " + device.vendorId + "\n")
                textBuffer.append("Product ID: " + device.productId);
                textBuffer.append("Interface count: " + device.interfaceCount + "\n")
                textBuffer.append("---------------------------------------" + "\n")

                // Get interface details
                for (index in 0 until device.interfaceCount) {
                    val mUsbInterface: UsbInterface = device.getInterface(index)
                    textBuffer.append("  *****     *****" + "\n")
                    textBuffer.append("  Interface index: " + index + "\n")
                    textBuffer.append("  Interface name: " + mUsbInterface.name + "\n")
                    textBuffer.append("  Interface ID: " + mUsbInterface.id + "\n")
                    textBuffer.append("  Inteface class: " + mUsbInterface.interfaceClass + "\n")
                    textBuffer.append("  Interface protocol: " + mUsbInterface.interfaceProtocol + "\n")
                    textBuffer.append("  Interface alternateSetting: " + mUsbInterface.alternateSetting + "\n")
                    textBuffer.append("  Endpoint count: " + mUsbInterface.endpointCount + "\n")
                    // Get endpoint details
                    for (epi in 0 until mUsbInterface.endpointCount) {
                        val mEndpoint: UsbEndpoint = mUsbInterface.getEndpoint(epi)
                        textBuffer.append("    ++++   ++++   ++++" + "\n")
                        textBuffer.append("    Endpoint index: " + epi + "\n")
                        textBuffer.append("    Address: " + mEndpoint.address + "\n")
                        textBuffer.append("    Attributes: " + mEndpoint.attributes + "\n")
                        textBuffer.append("    Direction: " + mEndpoint.direction + "\n")
                        textBuffer.append("    Number: " + mEndpoint.endpointNumber + "\n")
                        textBuffer.append("    Interval: " + mEndpoint.interval + "\n")
                        textBuffer.append("    Packet size: " + mEndpoint.maxPacketSize + "\n")
                        textBuffer.append("    Type: " + mEndpoint.type + "\n")
                    }
                }
            }
            if(textBuffer.isEmpty())
                textBuffer.append("No device found")
            textView.text = textBuffer.toString()
        })
    }
}

