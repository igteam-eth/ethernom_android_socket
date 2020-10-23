package com.ig.socket.tcpworkermanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ig.socket.TcpActivity.Companion.tcpSocket

class TCPSendWorker(context: Context, workerParams: WorkerParameters?) : Worker(context, workerParams!!) {
    override fun doWork(): Result {
        Log.d("SendWorker", "doWork")
        val data = inputData.getString("DATA_SEND")
        if (tcpSocket != null) tcpSocket!!.send(data!!)
        return Result.success()
    }

}