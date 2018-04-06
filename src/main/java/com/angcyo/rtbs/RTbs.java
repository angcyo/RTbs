package com.angcyo.rtbs;

import android.content.Context;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by angcyo on 2018/04/06 17:31
 */
public class RTbs {
    public static void init(Context context) {
        //腾讯TBS X5内核浏览器初始化
        try {
            QbSdk.initX5Environment(context.getApplicationContext(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
