package com.ig.socket.udpworkermanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ig.socket.UdpActivity.Companion.udpSocket

class UDPBindWorker(context: Context, workerParams: WorkerParameters?) : Worker(context, workerParams!!) {
    override fun doWork(): Result {
        Log.d("UDPWorker", "doWork")
        if (udpSocket != null) {
            udpSocket!!.bind()
            udpSocket!!.receive()
        }
        return Result.success()
    }

}
