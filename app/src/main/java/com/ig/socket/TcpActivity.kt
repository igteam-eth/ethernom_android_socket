package com.ig.socket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ig.socket.socket.UtilSocket.DATA_ADDRESS
import com.ig.socket.socket.UtilSocket.DATA_RECEIVE
import com.ig.socket.socket.UtilSocket.DATA_SEND
import com.ig.socket.socket.*
import com.ig.socket.socket.TCPSocket.Companion.TCP_CONNECT
import com.ig.socket.socket.TCPSocket.Companion.TCP_CLOSE
import com.ig.socket.socket.TCPSocket.Companion.TCP_CONNECT_FAIL
import com.ig.socket.socket.TCPSocket.Companion.TCP_RECEIVE
import com.ig.socket.socket.TCPSocket.Companion.TCP_SEND
import com.ig.socket.tcpworkermanager.TCPBindWorker
import com.ig.socket.tcpworkermanager.TCPCloseWorker
import com.ig.socket.tcpworkermanager.TCPSendWorker
import kotlinx.android.synthetic.main.activity_tcp.*
import java.util.*

class TcpActivity : AppCompatActivity() {
    private var msg:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp)

        tcpSocket = TCPSocket(this)
        registerReceiver(tcpCallback, intentFilter)
        //OneTimeWorkRequest
        val oneTCPTimeRequest = OneTimeWorkRequest.Builder(TCPBindWorker::class.java).addTag("TCP_WORK_MANAGER").build()
        WorkManager.getInstance(this).enqueue(oneTCPTimeRequest)
        btnTcpSend.setOnClickListener {
            val data = Data.Builder().putString("DATA_SEND", etTcpMessage.text.toString()).build()
            //OneTimeWorkRequest
            val oneSendTimeRequest = OneTimeWorkRequest.Builder(TCPSendWorker::class.java).addTag("SEND_TCP_WORK_MANAGER").setInputData(data).build()
            WorkManager.getInstance(this).enqueue(oneSendTimeRequest)
            tvTcpMessages.text = ""
        }
        btnTcpClose.setOnClickListener {
            //OneTimeWorkRequest
            val oneCloseTimeRequest = OneTimeWorkRequest.Builder(TCPCloseWorker::class.java).addTag("CLOSE_TCP_WORK_MANAGER").build()
            WorkManager.getInstance(this).enqueue(oneCloseTimeRequest)
            WorkManager.getInstance(this).cancelAllWorkByTag("TCP_WORK_MANAGER")
        }
    }

    private var tcpCallback: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (Objects.requireNonNull(intent.action)) {
                TCP_CONNECT -> {
                    val address = intent.getStringExtra(DATA_ADDRESS)
                    msg = "\nConnected -> $address"
                    tvTcpMessages.text = msg
                }
                TCP_CONNECT_FAIL -> {
                    val address = intent.getStringExtra(DATA_ADDRESS)
                    msg = "\nConnect Fail -> $address"
                    tvTcpMessages.text = msg
                }
                TCP_SEND -> {
                    val send = intent.getStringExtra(DATA_SEND)
                    msg += "\nSend: $send"
                    tvTcpMessages.text = msg
                }
                TCP_RECEIVE -> {
                    val receive = intent.getStringExtra(DATA_RECEIVE)
                    msg += "\nReceive: $receive"
                    tvTcpMessages.text = msg
                }
                TCP_CLOSE -> {
                    msg += "\nClosed"
                    tvTcpMessages.text = msg
                }
            }
        }
    }

    private val intentFilter: IntentFilter
        get() {
            val intentFilter = IntentFilter()
            intentFilter.addAction(TCP_CONNECT)
            intentFilter.addAction(TCP_SEND)
            intentFilter.addAction(TCP_RECEIVE)
            intentFilter.addAction(TCP_CLOSE)
            return intentFilter
        }

    override fun onStop() {
        super.onStop()
        WorkManager.getInstance(this).cancelAllWorkByTag("TCP_WORK_MANAGER")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(tcpCallback)
    }

    companion object {
        var tcpSocket: TCPSocket? = null
    }
}