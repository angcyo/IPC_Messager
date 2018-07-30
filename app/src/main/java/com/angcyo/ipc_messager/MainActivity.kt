package com.angcyo.ipc_messager

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val localHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            L.e("客户端: handleMessage ->${RemoteService.test_static_data} ${msg.what} ${msg.obj}")

            msg.data.classLoader = classLoader

            showText("收到服务端消息:${RemoteService.test_static_data} ${msg.what} ${msg.obj} ${msg.data.getParcelable<MsgBean>("data")}")
        }
    }

    val localMessenger = Messenger(localHandler)

    var remoteMessenger: Messenger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        L.e("进程名: onCreate -> ${Util.getProcessName(this)}")

        val remoteServiceIntent = Intent(this, RemoteService::class.java)
        val remoteCon = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {
                L.e("call: onServiceDisconnected -> $p0")

                showText("onServiceDisconnected_${p0}")

                remoteMessenger = null
            }

            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                L.e("call: onServiceConnected -> $p0 $p1")
                showText("onServiceConnected_${p0}")

                remoteMessenger = Messenger(p1)
                sendText(1, "客户端连接成功...")
            }
        }

        findViewById<View>(R.id.start).setOnClickListener {
            startService(remoteServiceIntent)
        }

        findViewById<View>(R.id.stop).setOnClickListener {
            stopService(remoteServiceIntent)
        }

        findViewById<View>(R.id.bind).setOnClickListener {
            bindService(remoteServiceIntent, remoteCon, Service.BIND_AUTO_CREATE)
        }

        findViewById<View>(R.id.unbind).setOnClickListener {
            remoteMessenger?.let {
                unbindService(remoteCon)
                remoteMessenger = null
            }
        }

        findViewById<View>(R.id.send).setOnClickListener {
            sendText(2, "客户端发送的测试消息:${RemoteService.test_static_data}_${df.format(Date(System.currentTimeMillis()))}")
        }

        findViewById<View>(R.id.sleep).setOnClickListener {
            sendText(3, "客户端发送的死循环:${RemoteService.test_static_data}_${df.format(Date(System.currentTimeMillis()))}")
        }
    }

    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
    private fun showText(str: String) {
        findViewById<TextView>(R.id.text_view).apply {
            text = "${df.format(Date(System.currentTimeMillis()))}\n${str}\n\n$text"
        }
    }

    private fun sendText(what: Int, text: String) {
        remoteMessenger?.send(Message.obtain(null, what /*, MsgBean(text)*/).apply {
            replyTo = localMessenger
            data = Bundle().apply {
                putParcelable("data", MsgBean(text))
            }
        })
    }
}
