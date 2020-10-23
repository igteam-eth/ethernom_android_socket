package com.ig.socket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ig.socket.socket.UtilSocket.DATA_ADDRESS
import com.ig.socket.socket.UtilSocket.DATA_RECEIVE
import com.ig.socket.socket.UtilSocket.DATA_SEND
import com.ig.socket.socket.UDPSocket
import com.ig.socket.socket.UDPSocket.Companion.UDP_BIND
import com.ig.socket.socket.UDPSocket.Companion.UDP_BIND_FAIL
import com.ig.socket.socket.UDPSocket.Companion.UDP_CLOSE
import com.ig.socket.socket.UDPSocket.Companion.UDP_RECEIVE
import com.ig.socket.socket.UDPSocket.Companion.UDP_SEND
import com.ig.socket.udpworkermanager.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class UdpActivity : AppCompatActivity() {
    private var msg:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        udpSocket = UDPSocket(this)
        registerReceiver(udpCallback, intentFilter)
        //OneTimeWorkRequest
        val oneUDPTimeRequest = OneTimeWorkRequest.Builder(UDPBindWorker::class.java).addTag("UDP_WORK_MANAGER").build()
        WorkManager.getInstance(this).enqueue(oneUDPTimeRequest)

        btnSend.setOnClickListener {
            val data = Data.Builder().putString("DATA_SEND", etMessage.text.toString()).build()
            //OneTimeWorkRequest
            val oneSendTimeRequest = OneTimeWorkRequest.Builder(UDPSendWorker::class.java).addTag("SEND_WORK_MANAGER").setInputData(data).build()
            WorkManager.getInstance(this).enqueue(oneSendTimeRequest)
            tvMessages.text = ""
        }
        btnClose.setOnClickListener { //OneTimeWorkRequest
            val oneCloseTimeRequest = OneTimeWorkRequest.Builder(UDPCloseWorker::class.java).addTag("CLOSE_WORK_MANAGER").build()
            WorkManager.getInstance(this).enqueue(oneCloseTimeRequest)
            WorkManager.getInstance(this).cancelAllWorkByTag("UDP_WORK_MANAGER")
        }
    }

    private var udpCallback: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (Objects.requireNonNull(intent.action)) {
                UDP_BIND -> {
                    val address = intent.getStringExtra(DATA_ADDRESS)
                    msg = "\nBind -> $address"
                    tvMessages.text = msg
                }
                UDP_BIND_FAIL -> {
                    val address = intent.getStringExtra(DATA_ADDRESS)
                    msg = "\nBind Fail -> $address"
                    tvMessages.text = msg
                }
                UDP_SEND -> {
                    val send = intent.getStringExtra(DATA_SEND)
                    msg += "\nSend: $send"
                    tvMessages.text = msg
                }
                UDP_RECEIVE -> {
                    val receive = intent.getStringExtra(DATA_RECEIVE)
                    msg += "\nReceive: $receive"
                    tvMessages.text = msg
                }
                UDP_CLOSE -> {
                    msg += "\nClosed"
                    tvMessages.text = msg
                }
            }
        }
    }

    private val intentFilter: IntentFilter
        get() {
            val intentFilter = IntentFilter()
            intentFilter.addAction(UDP_BIND)
            intentFilter.addAction(UDP_SEND)
            intentFilter.addAction(UDP_RECEIVE)
            intentFilter.addAction(UDP_CLOSE)
            return intentFilter
        }

    override fun onStop() {
        super.onStop()
        WorkManager.getInstance(this).cancelAllWorkByTag("UDP_WORK_MANAGER")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(udpCallback)
    }

    companion object {
        var udpSocket: UDPSocket? = null
    }
}