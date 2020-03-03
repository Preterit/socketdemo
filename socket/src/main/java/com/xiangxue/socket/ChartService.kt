package com.xiangxue.socket

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/**
 * Date:2020/3/1
 * author:lwb
 * Desc:
 */
fun main(args: Array<String>) {
    ChartService()
}

class ChartService {
    private var serviceSocket: ServerSocket? = null    //  服务器的 serviceSocket
    private var part = 10065  // 端口号
    private var mClients: ArrayList<Socket?> = arrayListOf()  // 客户端
    private var exPool = Executors.newCachedThreadPool()  // 线程池

    init {
        //开启服务
        serviceSocket = ServerSocket(part)
        //创建一个线程池
        var client: Socket? = null

        while (true) {
            println("等待客户上门。。。")
            client = serviceSocket?.accept()
            println("有客户来了客户是 ${client?.inetAddress}")
            mClients.add(client)
            exPool.execute(Service(client))
        }
    }

    inner class Service(private var socket: Socket?) : Runnable {

        private var br: BufferedReader? = null
        private var msg: String? = ""

        init {
            if (socket != null) {
                br = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            }
            sendMsg()
        }

        override fun run() {
            while (true) {
                msg = br?.readLine()
                if (msg != null) {
                    println("客户端说 --  ${msg}")

                    if ("bye" == msg) {
                        socket?.close()
                        break
                    } else if (msg!!.isNotEmpty()) {
                        sendMsg2()
                    }
                }
            }
        }

        fun sendMsg() {  // 回给客户端的消息
            var num = mClients.size
            if (socket != null) {
                val os = socket!!.getOutputStream()
                var pw = PrintWriter(os)
                pw.write("你是第 $num 个客户")
                pw.flush()
            }
        }

        fun sendMsg2() {
            var num = mClients.size
            if (socket != null) {
                val os = socket!!.getOutputStream()
                var pw = PrintWriter(os)
                pw.println("你就是个垃圾")
                pw.flush()
            }
        }
    }

}

