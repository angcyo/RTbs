package com.angcyo.rtbs;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.angcyo.uiview.base.UIIDialogRxImpl;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/02/28 17:01
 * 修改人员：Robi
 * 修改时间：2018/02/28 17:01
 * 修改备注：
 * Version: 1.0.0
 */
public class X5FileDownloadUIView extends UIIDialogRxImpl {

    /**
     * 需要下载的数据Bean
     */
    public DownloadFileBean mDownloadFileBean;

    /**
     * 页面关闭后, 的回调, 未点击下载, 数据为 null
     */
    public OnDownloadListener mDownloadListener;

    @Override
    protected View inflateDialogView(@NonNull FrameLayout dialogRootLayout, @NonNull LayoutInflater inflater) {
        return inflate(R.layout.dialog_x5_file_download);
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();
        if (mDownloadFileBean != null) {
            final String fileName = RUtils.getFileNameFromUrl(mDownloadFileBean.url);
            mViewHolder.tv(R.id.target_url_view).setText(mDownloadFileBean.url);
            mViewHolder.tv(R.id.file_name_view).setText(fileName);
            mViewHolder.tv(R.id.file_size_view).setText(RUtils.formatFileSize(mDownloadFileBean.fileSize));
            mViewHolder.tv(R.id.file_type_view).setText(mDownloadFileBean.fileType);

            mViewHolder.click(R.id.download_button, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIParam uiParam = new UIParam();
                    uiParam.getBundle().putBoolean("IS_DOWNLOAD", true);
                    finishIView(uiParam);

                    if (RUtils.downLoadFile(mActivity, mDownloadFileBean.url) != -1) {
                        T_.show("开始下载:" + fileName);
                        //goBack();
                    }
                }
            });
        }
    }

    @Override
    public void onViewUnload(UIParam uiParam) {
        super.onViewUnload(uiParam);
        if (uiParam.getBundle().getBoolean("IS_DOWNLOAD", false)) {
            if (mDownloadListener != null) {
                mDownloadListener.onDownload(mDownloadFileBean);
            }
        } else {
            if (mDownloadListener != null) {
                mDownloadListener.onDownload(null);
            }
        }
    }

    public interface OnDownloadListener {
        void onDownload(DownloadFileBean bean);
    }
}
