package com.angcyo.ipc_messager

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.*

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/07/30 13:36
 * 修改人员：Robi
 * 修改时间：2018/07/30 13:36
 * 修改备注：
 * Version: 1.0.0
 */
class RemoteService : Service() {

    val remoteHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            test_static_data = 200

            msg.data.classLoader = RemoteService.javaClass.classLoader

            L.e("服务端: handleMessage ->$test_static_data ${msg.what} ${msg.obj} ${msg.replyTo} ${msg.data.getParcelable<MsgBean>("data")}") // ${msg.data.getParcelable<MsgBean>("data")}

            if (msg.what == 3) {
                //测试死循环, 不会出现anr
                Thread.sleep(10_000)
            }

            msg.replyTo?.let {

                //java.lang.RuntimeException: Can't marshal callbacks across processes.
//                it.send(Message.obtain(null) {
//                    L.e("runnable")
//                })

                //it.send(Message.obtain(null, 10_000 /*, MsgBean("服务端已收到消息.")*/))
                sendText(it, 10_001, "服务端已收到消息.${msg.what}")
            }
        }
    }

    private fun sendText(messenger: Messenger, what: Int, text: String) {
        messenger.send(Message.obtain(null, what /*, MsgBean(text)*/).apply {
            replyTo = remoteMessenger
            data = Bundle().apply {
                putParcelable("data", MsgBean(text))
            }
        })
    }

    private val remoteMessenger: Messenger

    companion object {
        /*静态数据共享测试, 静态数据不会共享哦*/
        var test_static_data = 100
    }

    init {
        remoteMessenger = Messenger(remoteHandler)
    }

    override fun onBind(p0: Intent?): IBinder {
        L.e("call: onBind -> $p0")
        return remoteMessenger.binder
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        L.e("call: onConfigurationChanged -> ")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        L.e("call: onRebind -> ")
    }

    /*每启动1次, startId自增*/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        L.e("call: onStartCommand -> $intent $flags $startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        L.e("call: onCreate -> ")

        L.e("进程名: onCreate -> ${Util.getProcessName(this)}")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        L.e("call: onLowMemory -> ")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        L.e("call: onStart -> ")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        L.e("call: onTaskRemoved -> ")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        L.e("call: onTrimMemory -> ")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        L.e("call: onUnbind -> ")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.e("call: onDestroy -> ")
    }
}