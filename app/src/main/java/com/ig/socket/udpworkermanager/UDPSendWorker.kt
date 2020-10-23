package com.ig.socket.udpworkermanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ig.socket.UdpActivity.Companion.udpSocket

class UDPSendWorker(context: Context, workerParams: WorkerParameters?) : Worker(context, workerParams!!) {
    override fun doWork(): Result {
        Log.d("SendWorker", "doWork")
        val data = inputData.getString("DATA_SEND")
        if (udpSocket != null) udpSocket!!.send(data!!)
        return Result.success()
    }

}