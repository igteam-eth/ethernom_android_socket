package com.ig.socket.socket

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ig.socket.socket.UtilSocket.DATA_ADDRESS
import com.ig.socket.socket.UtilSocket.DATA_CLOSE
import com.ig.socket.socket.UtilSocket.DATA_RECEIVE
import com.ig.socket.socket.UtilSocket.DATA_SEND
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

class TCPSocket(ctx: Context) {
    private val TAG = "TCPSocket"
    private var context = ctx
    private lateinit var tcpSocket:Socket
    private lateinit var output: DataOutputStream
    private lateinit var input: DataInputStream
    private var connected:Boolean = false

    fun open () {
        try {
            tcpSocket = Socket() // Initial Socket
        } catch (e: IOException) {
            Log.d(TAG, "Open Error: ${e.message}")
        }
    }

    fun connect() {
        try {
            val serverAddress: SocketAddress = InetSocketAddress("192.168.5.209", 7070) // Server Socket IP  & Port
            tcpSocket.connect(serverAddress) // Connect to Socket server
            output = DataOutputStream(tcpSocket.getOutputStream()) // Initial Output stream for send data to socket server
            input  = DataInputStream(tcpSocket.getInputStream()) // Initial Input stream for receive data from socket server
            connected = true
            sendBroadcast(TCP_CONNECT, DATA_ADDRESS, tcpSocket.localSocketAddress.toString()) // send data to broadcast to receiver
        } catch (e: IOException) {
            Log.d(TAG, "Connect Error: ${e.message}")
            connected = false
            sendBroadcast(TCP_CONNECT_FAIL, DATA_ADDRESS, tcpSocket.localSocketAddress.toString())
        }
    }

    fun receive() {
        while(connected){
            try {
                val data = ByteArray(8000)
                input.read(data) // Read data from socket
                if (data.isNotEmpty()) {
                    val msg = UtilSocket.trim(data).toString(Charsets.UTF_8)
                    sendBroadcast(TCP_RECEIVE, DATA_RECEIVE, msg) // Send data to broadcast to receiver
                } else {
                    close()
                    break
                }
            }catch (e: IOException) {
                Log.d(TAG, "Receive Error: ${e.message}")
                close()
            }
        }
    }

    fun send (data :String) {
        if(connected) {
            try {
                val msg = data.toByteArray(Charsets.UTF_8)  // Convert ascii to byte array
                output.write(msg) // writ data to socket
                output.flush()
                sendBroadcast(TCP_SEND, DATA_SEND, data) // send data to broadcast to receiver
            } catch (e: IOException) {
                Log.d(TAG, "Send Error: ${e.message}")
                close()
            }
        }
    }

    fun close () {
        try {
            if (connected) {
                connected = false
                input.close() // Close input stream
                output.close() // Close output stream
                tcpSocket.close() // Close socket
                sendBroadcast(TCP_CLOSE, DATA_CLOSE, "close")  // send data to broadcast to receiver
            }
        } catch (e: IOException) {
            Log.d(TAG, "Close Error: ${e.message}")
            connected = false
        }
    }

    private fun sendBroadcast(action:String, key:String, value:String) {
        val intent = Intent(action)
        intent.putExtra(key, value)
        context.sendBroadcast(intent)
    }

    companion object {
        const val TCP_CONNECT      = "TCP_BIND"
        const val TCP_CONNECT_FAIL = "TCP_BIND_FAIL"
        const val TCP_SEND         = "TCP_SEND"
        const val TCP_RECEIVE      = "TCP_RECEIVE"
        const val TCP_CLOSE        = "TCP_CLOSE"
    }
}