package com.angcyo.rtbs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarItem;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.widget.EmptyView;
import com.angcyo.uiview.widget.RTextView;
import com.angcyo.uiview.widget.SimpleProgressBar;
import com.tencent.smtt.sdk.WebView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：浏览网页的界面
 * 创建人员：Robi
 * 创建时间：2017/02/15 17:23
 * 修改人员：Robi
 * 修改时间：2017/02/15 17:23
 * 修改备注：
 * Version: 1.0.0
 */
public class X5WebUIView extends UIContentView {

    protected X5WebView mWebView;
    protected String mTargetUrl;
    SimpleProgressBar mProgressBarView;
    EmptyView mEmptyView;
    WebCallback mWebCallback;
    private int mSoftInputMode;
    private X5RefreshLayout mRefreshLayout;

    private boolean showDefaultMenu = true;
    /**
     * 正在处理的下载链接
     */
    private String downloadUrl = "";

    public X5WebUIView(String targetUrl) {
//        String encode = targetUrl;
//        try {
//            if (targetUrl.contains("klgwl.com") || targetUrl.contains("120.78.182.253")) {
//                mTargetUrl = encode;
//                return;
//            } else {
//                encode = URLEncoder.encode(targetUrl, "utf8");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //mTargetUrl = Api.Companion.h5Url("/link/to?url=" + encode);
        mTargetUrl = targetUrl;
    }

    public static String createJSParams(String data, int result) {
        StringBuilder builder = new StringBuilder();
        builder.append("'{");

        builder.append("\"");
        if (result > 0) {
            builder.append("data");
        } else {
            builder.append("error");
        }

        builder.append("\"");
        builder.append(":");
        builder.append(data);
        builder.append(",");

        builder.append("\"");
        builder.append("result");
        builder.append("\"");
        builder.append(":");
        builder.append(result);

        builder.append("}'");
        return builder.toString();
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        TitleBarPattern titleBarPattern = super.getTitleBar()
                .setShowBackImageView(false)
                .setTitleStringLength(15)
                .setTitleString("");
        //.setTitleBarBGColor(Color.TRANSPARENT)
        //.setFloating(true)
//        titleBarPattern.addLeftItem(TitleBarPattern.buildImage(R.drawable.guanbi_button, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finishIView();
//            }
//        }).setVisibility(View.GONE));

        titleBarPattern.addLeftItem(TitleBarPattern.buildText("返回", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackPressed()) {
                    finishIView();
                }
            }

        }).setOnItemInitListener(new TitleBarItem.OnItemInitListener() {
            @Override
            public void onItemInit(View itemView, TitleBarItem item) {
                if (itemView instanceof TextView) {
                    RTextView.setLeftIco((TextView) itemView, R.drawable.base_back);
                }
            }
        }));


        titleBarPattern.addLeftItem(TitleBarPattern.buildText("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        }).setVisibility(View.GONE));

        if (showDefaultMenu) {
            titleBarPattern
                    .addRightItem(TitleBarPattern.buildImage(R.drawable.base_more, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIBottomItemDialog.build()
                                    .addItem("在浏览器中打开", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mWebView != null) {
                                                RUtils.openUrl(mActivity, mWebView.getUrl());
                                            }
                                        }
                                    })
                                    .addItem("刷新", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mWebView != null) {
                                                mWebView.reload();
                                            }
                                        }
                                    })
                                    .showDialog(X5WebUIView.this);
                        }
                    }))
            ;
        }

        return titleBarPattern;
    }

    public X5WebUIView setShowDefaultMenu(boolean showDefaultMenu) {
        this.showDefaultMenu = showDefaultMenu;
        return this;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        //inflate(R.layout.view_x5_web);
        mEmptyView = new EmptyView(mActivity);
        int offset = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        mEmptyView.setPadding(offset, offset, offset, offset);
        baseContentLayout.addView(mEmptyView, new ViewGroup.LayoutParams(-1, -1));
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        inflate(R.layout.base_view_x5_web);
        //mEmptyView = mViewHolder.v(R.id.empty_view);
        mWebView = mViewHolder.v(R.id.web_view);
//        WebSettings settings = mWebView.getSettings();
//        settings.setUserAgent(settings.getUserAgentString() + " KLG_Android");

        mProgressBarView = mViewHolder.v(R.id.progress_bar_view);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);

//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
//                webView.getSettings().setDefaultTextEncodingName("utf-8");
//                webView.loadUrl(url);
//                //getUITitleBarContainer().evaluateBackgroundColorSelf(webView.getScrollY());
//                return true;
//            }
//        });

//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView webView, int progress) {
//                super.onProgressChanged(webView, progress);
//                mProgressBarView.setProgress(progress);
//                if (progress >= 90) {
//                    mEmptyView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onReceivedTitle(WebView webView, String title) {
//                super.onReceivedTitle(webView, title);
//                setTitleString(title);
//            }
//        });

        initWebView();

        onLoadUrl();

        if (L.LOG_DEBUG) {
            mViewHolder.tv(R.id.url_view).setVisibility(View.VISIBLE);
            mViewHolder.tv(R.id.url_view).setText(mTargetUrl + "\n" + mWebView.getSettings().getUserAgentString());
        }
    }

    protected void initWebView() {
        mWebView.setOnWebViewListener(new X5WebView.OnWebViewListener() {
            @Override
            public void onScroll(int left, int top, int dx, int dy) {
                //getUITitleBarContainer().evaluateBackgroundColorSelf(top);
                //L.e("call: onScroll([left, top, dx, dy])-> " + top);
            }

            @Override
            public void onOverScroll(int scrollY) {
                //L.e("call: onOverScroll([scrollY])-> " + scrollY);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                X5WebUIView.this.onPageFinished(webView, url);
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                setTitleString(title);
            }

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                X5WebUIView.this.onProgressChanged(webView, progress);
            }
        });

        mWebView.addJavascriptInterface(new AndroidJs() {

//            @JavascriptInterface
//            public void getCurrentUser(String appid) {
//                String url = mWebView.getUrl();
//                L.e(url + " getVerifyToken " + appid);
//                Uri parse = Uri.parse(url);
//                if (!TextUtils.isEmpty(url) &&
//                        (parse.getHost().contains("klgwl.com") ||
//                                parse.getHost().contains("120.78.182.253"))) {
//                    X5WebUIView.this.getVerifyToken(appid);
//                }
//            }
        }, "android");

        mWebView.setMyDownloadListener(new X5WebView.MyDownloadListener() {
            @Override
            public void onDownloadStart(String url /*下载地址*/, String userAgent /*user agent*/,
                                        String contentDisposition, String mime /*文件mime application/zip*/,
                                        long length /*文件大小 kb*/) {
                if (TextUtils.equals(downloadUrl, url)) {

                } else {
                    downloadUrl = url;
                    X5FileDownloadUIView dialog = new X5FileDownloadUIView();
                    dialog.mDownloadFileBean = new DownloadFileBean();
                    dialog.mDownloadFileBean.url = url;
                    dialog.mDownloadFileBean.userAgent = userAgent;
                    dialog.mDownloadFileBean.fileType = mime;
                    dialog.mDownloadFileBean.fileSize = length;
                    dialog.mDownloadFileBean.fileName = RUtils.getFileNameFromAttachment(contentDisposition);
                    dialog.mDownloadFileBean.contentDisposition = contentDisposition;
                    dialog.mDownloadListener = new X5FileDownloadUIView.OnDownloadListener() {
                        @Override
                        public void onDownload(DownloadFileBean bean) {
                            downloadUrl = "";
                            if (bean == null) {

                            }
                        }
                    };
                    mParentILayout.startIView(dialog);
                }
            }
        });

        mWebView.setOnOpenAppListener(new X5WebView.OnOpenAppListener() {
            @Override
            public void onOpenApp(final RUtils.QueryAppBean appBean) {
                UIDialog.build()
                        .setDialogContent("请求打开应用")
                        .setOkText("打开")
                        .setOkClick(new UIDialog.OnDialogClick() {
                            @Override
                            public void onDialogClick(UIDialog dialog, View clickView) {
                                mActivity.startActivity(appBean.startIntent);
                            }
                        })
                        .setOnInitDialogContent(new UIIDialogImpl.OnInitDialogContent() {
                            @Override
                            public void onInitDialogContent(@NonNull UIIDialogImpl dialog, @NonNull RBaseViewHolder viewHolder) {
                                RTextView contentView = viewHolder.v(R.id.base_dialog_content_view);
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
                                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                                contentView.setLayoutParams(layoutParams);

                                RTextView topContentView = viewHolder.v(R.id.base_dialog_top_content_view);
                                topContentView.setGravity(Gravity.CENTER_HORIZONTAL);
                                topContentView.setVisibility(View.VISIBLE);
                                topContentView.setText(appBean.mAppInfo.appName);
                                RTextView.setTopIco(topContentView, appBean.mAppInfo.appIcon);
                                layoutParams = (LinearLayout.LayoutParams) topContentView.getLayoutParams();
                                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                                topContentView.setLayoutParams(layoutParams);
                            }
                        })
                        .showDialog(mParentILayout);
            }
        });
    }

    public void invokeJs(String method, String params) {
        String js = "javascript:" + method + "(" + params + ");";
        L.e("call: invokeJs -> " + js);
        mWebView.loadUrl(js);
    }

    protected void onLoadUrl() {
        L.i("call: onLoadUrl([])-> 加载网页:" + mTargetUrl);
        mWebView.loadUrl(mTargetUrl);
        mProgressBarView.setProgress(10);
    }

    protected void onPageFinished(WebView webView, String url) {
        if (mWebCallback != null) {
            mWebCallback.onPageFinished(webView, url);
        }
        if (mWebView.canGoBack()) {
            getUITitleBarContainer().showLeftItem(2);
        } else {
            getUITitleBarContainer().hideLeftItem(2);
        }
        //L.e("call: onPageFinished([webView, url])-> ");
    }

    protected void onProgressChanged(WebView webView, int progress) {
        mProgressBarView.setProgress(progress);
        if (progress >= 90) {
            mEmptyView.setVisibility(View.GONE);
            //L.e("call: onProgressChanged([webView, progress])-> " + progress);
        }
        if (mWebCallback != null) {
            mWebCallback.onProgressChanged(webView, progress);
        }
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mSoftInputMode = mActivity.getWindow().getAttributes().softInputMode;

        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
            mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
    }

    @Override
    public void onViewShow(long viewShowCount) {
        super.onViewShow(viewShowCount);
        mWebView.onResume();
        mWebView.resumeTimers();
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mWebView.onPause();
        mWebView.pauseTimers();
        mActivity.getWindow().setSoftInputMode(mSoftInputMode);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mWebView.destroy();
    }

    @Override
    public boolean onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            if (mWebView.canGoBack()) {
                getUITitleBarContainer().showLeftItem(2);
            } else {
                getUITitleBarContainer().hideLeftItem(2);
            }
            return false;
        }
        return true;
    }

    public X5WebUIView setWebCallback(WebCallback webCallback) {
        mWebCallback = webCallback;
        return this;
    }

    public static abstract class WebCallback {
        public void onPageFinished(WebView webView, String url) {

        }

        public void onProgressChanged(WebView webView, int progress) {
        }
    }
}
