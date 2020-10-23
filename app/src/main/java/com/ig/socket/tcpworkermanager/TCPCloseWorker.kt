package com.ig.socket.tcpworkermanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ig.socket.TcpActivity.Companion.tcpSocket

class TCPCloseWorker(context: Context, workerParams: WorkerParameters?) : Worker(context, workerParams!!) {
    override fun doWork(): Result {
        Log.d("CloseWorker", "doWork")
        if (tcpSocket != null) tcpSocket!!.close()
        return Result.success()
    }

}