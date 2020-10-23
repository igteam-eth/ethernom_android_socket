package com.ig.socket.udpworkermanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ig.socket.UdpActivity.Companion.udpSocket

class UDPCloseWorker(context: Context, workerParams: WorkerParameters?) : Worker(context, workerParams!!) {
    override fun doWork(): Result {
        Log.d("CloseWorker", "doWork")
        if (udpSocket != null) udpSocket!!.close()
        return Result.success()
    }

}