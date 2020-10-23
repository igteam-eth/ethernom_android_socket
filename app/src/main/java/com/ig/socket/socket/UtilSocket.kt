package com.ig.socket.socket

object  UtilSocket {
    const val DATA_ADDRESS = "DATA_ADDRESS"
    const val DATA_SEND    = "DATA_SEND"
    const val DATA_RECEIVE = "DATA_RECEIVE"
    const val DATA_CLOSE   = "DATA_CLOSE"

    fun trim(bytes: ByteArray): ByteArray { // Trim byte array space
        var i = bytes.size - 1
        while (i >= 0 && bytes[i].toInt() == 0) {
            --i
        }
        return bytes.copyOf(i + 1)
    }
}