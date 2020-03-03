package com.xiangxue.socketdemo

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.Socket
import java.util.concurrent.LinkedBlockingDeque


class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.name

    //定义相关变量,完成初始化
    private val HOST = "192.168.0.103"
    private val PORT = 10065

    private val sb = StringBuffer()
    private var socket: Socket? = null
    private var br: BufferedReader? = null
    private var out: PrintWriter? = null
    private var content: String? = "wo cao ni ma \n   ni ma de"
    private var writerFlag: Boolean = false  // 开启写线程的标示
    private val msgs = LinkedBlockingDeque<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvIp.text = content

        btnSend.setOnClickListener {
            if (!writerFlag) {
                Thread(writeRunnable).start()
            }
            writerFlag = true

            val msg: String = edit.text.toString()
            msgs.put(msg)
        }

        //网络操作不能放在UI主线程 4.0
        Thread(
            Runnable {
                try {
                    //和服务器建立连接
                    socket = Socket(HOST, PORT)
                    //获取输入流  读取从服务端发送过来的数据
                    br = BufferedReader(InputStreamReader(socket?.getInputStream()))
                    //获取输出流  向服务器中写入数据
                    out = PrintWriter(BufferedWriter(OutputStreamWriter(socket?.getOutputStream())))
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                    e.printStackTrace()
                } finally {

                }
            }
        ).start()

        Thread(readRunnable).start()
    }

    /**
     * 从服务器中读取数据的线程
     */
    private val readRunnable = Runnable {
        while (true) {
            if (socket == null) continue
            if (socket!!.isConnected && !socket!!.isInputShutdown) {
                content = br?.readLine()
                if (content != null) {
                    content += "\n"
                    //  通过Handler发送消息到主线程中
                    handler.sendEmptyMessage(123)
                }
            }
        }
    }
    /**
     * 往服务器 中写入数据的线程
     */
    private val writeRunnable = Runnable {
        while (true) {
            if (socket == null) break
            if (socket!!.isConnected && !socket!!.isOutputShutdown) {
                out?.println(msgs.take())
                out?.flush()
            }
        }
    }

    //定义一个handler对象,用来刷新界面
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                123 -> {
                    sb.append(content)
                    tvShow.text = sb.toString()
                }
            }
        }
    }
}
