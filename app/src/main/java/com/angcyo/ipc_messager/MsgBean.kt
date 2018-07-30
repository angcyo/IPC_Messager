package com.angcyo.ipc_messager

import android.os.Parcel
import android.os.Parcelable

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/07/30 16:00
 * 修改人员：Robi
 * 修改时间：2018/07/30 16:00
 * 修改备注：
 * Version: 1.0.0
 */
class MsgBean : Parcelable {
    var message = ""

    constructor(parcel: Parcel) : this() {
        message = parcel.readString()
    }

    constructor(message: String) {
        this.message = message
    }

    constructor() {}

    override fun toString(): String {
        return message
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MsgBean> {
        override fun createFromParcel(parcel: Parcel): MsgBean {
            return MsgBean(parcel)
        }

        override fun newArray(size: Int): Array<MsgBean?> {
            return arrayOfNulls(size)
        }
    }


}
