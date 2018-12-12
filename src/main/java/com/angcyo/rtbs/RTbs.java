package com.angcyo.rtbs;

import android.content.Context;
import com.angcyo.lib.L;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by angcyo on 2018/04/06 17:31
 */
public class RTbs {
    public static void init(Context context) {
        //腾讯TBS X5内核浏览器初始化
        try {
            QbSdk.initX5Environment(context.getApplicationContext(), new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {
                    L.i("腾讯X5 onCoreInitFinished");
                }

                @Override
                public void onViewInitFinished(boolean b) {
                    L.i("腾讯X5 onViewInitFinished:" + b);
                }
            });

            QbSdk.setTbsListener(new TbsListener() {
                @Override
                public void onDownloadFinish(int i) {
                    L.i("腾讯X5 onDownloadFinish:" + i);
                }

                @Override
                public void onInstallFinish(int i) {
                    L.i("腾讯X5 onInstallFinish:" + i);
                }

                @Override
                public void onDownloadProgress(int i) {
                    L.i("腾讯X5 onDownloadProgress:" + i);
                }
            });

            L.i("腾讯X5 canLoadX5:" + QbSdk.canLoadX5(context) +
                    " isTbsCoreInited:" + QbSdk.isTbsCoreInited());

            QbSdk.checkTbsValidity(context);

        } catch (Exception e) {
            L.i("腾讯X5 " + e.getMessage());
            e.printStackTrace();
        }
    }
}
