package com.ig.socket.socket

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ig.socket.socket.UtilSocket.DATA_ADDRESS
import com.ig.socket.socket.UtilSocket.DATA_CLOSE
import com.ig.socket.socket.UtilSocket.DATA_RECEIVE
import com.ig.socket.socket.UtilSocket.DATA_SEND
import java.io.IOException
import java.net.*

class UDPSocket(ctx: Context) {
    private val TAG = "UDPSocket"
    private lateinit var udpSocket: DatagramSocket
    private var bind = false
    private var context: Context = ctx

    fun bind() {
        try {
            udpSocket = DatagramSocket(null) // Initial socket
            val socketAddress: SocketAddress = InetSocketAddress("192.168.4.218", 4444) // Client Socket IP & Port
            udpSocket.bind(socketAddress) // Bind socket
            bind = true
            sendBroadcast(UDP_BIND, DATA_ADDRESS, udpSocket.localSocketAddress.toString()) // Send data broadcast to receiver
        } catch (e: SocketException) {
            Log.d(TAG, "Bind Error: ${e.message}")
            sendBroadcast(UDP_BIND_FAIL, DATA_ADDRESS, udpSocket.localSocketAddress.toString()) // Send data broadcast to receiver
            close()
        }
    }

    fun receive() {
        while (bind) {
            try {
                val message = ByteArray(8000)
                val packet = DatagramPacket(message, message.size) // Initial Datagram packet for receive data from socket
                udpSocket.receive(packet) // Receive data from socket
                if(message.isNotEmpty()) {
                    val text = UtilSocket.trim(message).toString(Charsets.UTF_8) // Convert byte array to ascii
                    sendBroadcast(UDP_RECEIVE, DATA_RECEIVE, text) // Send data broadcast to receiver
                } else {
                    close()
                    break
                }
            } catch (e: IOException) {
                Log.d(TAG, "Receive Error: ${e.message}")
                close()
            }
        }
    }

    fun send(data: String) {
        if (bind) {
            try {
                val serverAddress: SocketAddress = InetSocketAddress("188.166.233.13", 8080) // Server Socket IP & Port
                val buf: ByteArray = data.toByteArray(Charsets.UTF_8) // Convert ascii to byte array
                val packet = DatagramPacket(buf, buf.size, serverAddress) // Initial Datagram packet for send socket
                udpSocket.send(packet) // Send data to socket
                sendBroadcast(UDP_SEND, DATA_SEND, data) // Send data broadcast to receiver
            } catch (e: Exception) {
                Log.d(TAG, "Send Error: ${e.message}")
                close()
            }
        }
    }

    fun close() {
        if (bind) {
            try {
                bind = false
                udpSocket.close() // Close socket
                sendBroadcast(UDP_CLOSE, DATA_CLOSE, "close")
            } catch (e:IOException) {
                Log.d(TAG, "Close Error: ${e.message}")
                bind = false
            }
        }
    }

    private fun sendBroadcast(action:String, key:String, value:String) {
        val intent = Intent(action)
        intent.putExtra(key, value)
        context.sendBroadcast(intent)
    }

    companion object {
        const val UDP_BIND      = "UDP_BIND"
        const val UDP_BIND_FAIL = "UDP_BIND_FAIL"
        const val UDP_SEND      = "UDP_SEND"
        const val UDP_RECEIVE   = "UDP_RECEIVE"
        const val UDP_CLOSE     = "UDP_CLOSE"
    }
}