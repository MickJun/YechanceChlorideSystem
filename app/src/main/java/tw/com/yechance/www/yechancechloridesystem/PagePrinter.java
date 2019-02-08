package tw.com.yechance.www.yechancechloridesystem;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import jiuiv5.ICallback;
import jiuiv5.IWoyouService;

public class PagePrinter extends AppCompatActivity {


    WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_printer);
        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        mWebView = (WebView) findViewById(R.id.wv_view);

        // 设置编码
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        // 支持js
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        // 设置背景颜色 透明
        mWebView.setBackgroundColor(Color.rgb(96, 96, 96));
        mWebView.setWebViewClient(new WebViewClientDemo());//添加一个页面相应监听类
        // 载入包含js的html
        mWebView.loadData("", "text/html", null);
        mWebView.loadUrl("file:///android_asset/test.html");

        Intent intent_P = new Intent();
        intent_P.setPackage("jiuiv5");
        intent_P.setAction("jiuiv5.IWoyouService");
        startService(intent_P);//启动打印服务
        bindService(intent_P, connService, Context.BIND_AUTO_CREATE);
    }


    class WebViewClientDemo extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            /**
             * 注册JavascriptInterface，其中"lee"的名字随便取，如果你用"lee"，那么在html中只要用  lee.方法名()
             * 即可调用MyJavascriptInterface里的同名方法，参数也要一致
             */
            mWebView.addJavascriptInterface(new JsObject(), "lee");
        }

    }

    class JsObject {

        @JavascriptInterface
        public void funAndroid(final String i) {
            Toast.makeText(getApplicationContext(), "通过JS调用本地方法funAndroid " + i,	Toast.LENGTH_SHORT).show();

            try {
                //woyouService.printerSelfChecking(callback);//这里使用的AIDL方式打印
                woyouService.printTextWithFont("商⽶\n","",36,callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private IWoyouService woyouService;

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    ICallback callback = new ICallback.Stub() {

        @Override
        public void onRunResult(boolean success) throws RemoteException {
        }

        @Override
        public void onReturnString(final String value) throws RemoteException {
        }

        @Override
        public void onRaiseException(int code, final String msg)
                throws RemoteException {
        }
    };
}
